import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/*
	TODO: Synchronization, Threads
*/

public class Broker {

    static List<Broker> brokers;
    static List<Subscriber> registeredSubscribers;
    static List<Publisher> registeredPublishers;
    static HashMap<Topic, Value> HM;
    static ArrayList<String> Topics = new ArrayList<>();
    static int portid;
    static String myIP;
    static ServerSocket providerSocket = null;
    static HashMap<String, String> IPPORT;

    public static void main(String[] args) {
        System.out.println("Which broker is this?Give 1 for first 2 for second 3 for third");
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        init(10240 + (choice - 1));
        calculateKeys(10240 + (choice - 1));
        new Broker().acceptConnections();
    }

    public static void init(int i) {
        Broker.portid = i;
        try {
            providerSocket = new ServerSocket(portid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void acceptConnections() {
        System.out.println("Server with socket " + portid + " is opening...");
        while (true) {
            Socket connection;
            try {
                connection = providerSocket.accept();
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                Message temp = (Message) in.readObject();
                if (temp.getPubSub() == 3) {
                    System.out.println("PHRE KWDIKO 3");
                    System.out.println(temp.busline);
                    notify(Integer.parseInt(temp.busline));
                } else if (temp.getPubSub() == 4) {
                    System.out.println(temp);
                    System.out.println("TYPOU 4");
                    for (String key : IPPORT.keySet()) {
                        try {
                            Socket innercontact = new Socket(InetAddress.getByName("localhost"), Integer.parseInt(key));
                            ObjectOutputStream dis = new ObjectOutputStream(innercontact.getOutputStream());
                            dis.writeObject(new Message(3, temp.data, " Should send the topics to this port"));
                            dis.flush();
                        } catch (Exception e) {
                        }

                    }
                    notify(Integer.parseInt(temp.data));
                }
                connection.close();
            } catch (Exception e) {
                System.err.println("paok");
                continue;
            }
        }
    }

    public static void calculateKeys(int portid) {
        HashMap<String, String> Buslines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        IPPORT = FileReaders.readBusLines(new File("Brokers.txt"));
        Set<String> keys = Buslines.keySet();
        HashMap<String, BigInteger> digestsofPort = new HashMap<>();
        BigInteger max = BigInteger.ZERO;
        for (String key : IPPORT.keySet()) {
            if (key.equals(portid + ""))
                myIP = IPPORT.get(key);
            digestsofPort.put(key, new BigInteger(md5.getMd5(IPPORT.get(key) + key), 16));
            if (digestsofPort.get(key).compareTo(max) > 0) {
                max = digestsofPort.get(key);
            }
        }
        for (String key : Buslines.keySet()) {
            if (((new BigInteger(md5.getMd5(key), 16)).mod(max)).compareTo(new BigInteger(md5.getMd5(myIP + portid), 16)) <= 0) {
                Topics.add(key);
                for (String port : digestsofPort.keySet()) {
                    if (!port.equals(portid + "")) {
                        if (((new BigInteger(md5.getMd5(key), 16)).mod(max)).compareTo(digestsofPort.get(port)) <= 0 && (new BigInteger(md5.getMd5(myIP + portid), 16)).compareTo(digestsofPort.get(port)) > 0) {
                            Topics.remove(key);
                        }
                    }
                }
            }
        }
        IPPORT.remove(portid + "");
    }

    public static void notify(int Port) {
        System.out.println(portid);
        ObjectOutputStream out;
        try {
            Socket innercontact = new Socket(InetAddress.getByName("localhost"), Port);
            out = new ObjectOutputStream(innercontact.getOutputStream());
            Message info = new Message(Topics, portid);
            System.out.println(info.topics);
            out.writeObject(info);
            out.flush();
        } catch (Exception e) {
        }
    }

    /*

    public Publisher acceptConnection(Publisher publisher) {
        registeredPublishers.add(publisher);
        return publisher;
    }

    public Subscriber acceptConnection(Subscriber subscriber) {
        registeredSubscribers.add(subscriber);
        return subscriber;
    }

    public void pull(Topic topic) {
        Value Message;
        for (Subscriber subs : registeredSubscribers) {
            Message = HM.get(topic);
        }
    }

    public void connect() {
    }

    public void disconnect() {
    }

     */

}