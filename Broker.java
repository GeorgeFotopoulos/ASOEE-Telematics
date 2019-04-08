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
    static boolean leak=false;
    static List<Broker> brokers;
    static List<Subscriber> registeredSubscribers;
    static List<Publisher> registeredPublishers;
    static HashMap<String, String> HM = new HashMap<>();
    static ArrayList<String> Topics = new ArrayList<>();
    static int portid;
    static String myIP;
    static ServerSocket providerSocket = null;
    static HashMap<String, String> IPPORT;

    public static void main(String[] args) {
        System.out.println("Which broker is this?Give 1 for first 2 for second 3 for third");
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        init(10256 + (choice - 1));
        calculateKeys(10256 + (choice - 1));
        acceptConnections();
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
                System.out.println("New Connection");
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                System.out.println("pasda");
                Thread t = new ClientHandler(connection, in, out);

                // Invoking the start() method
                t.start();
                //t.join();
                // System.out.println(temp.data);
                // if(temp.getPubSub() == 1){
                //     while(true){
                //         temp = (Message) in.readObject();
                //         System.out.println(temp.data);
//
                //     }
                // }


            } catch (IOException e) {
                e.printStackTrace();

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
        System.out.println(Topics);
        IPPORT.remove(portid + "");
    }

    public static synchronized void notify(int Port) {
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



    public void connect() {
    }

    public void disconnect() {
    }

     */

}