import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Broker {
    //static List<Broker> brokers;
    //static List<Subscriber> registeredSubscribers;
    //static List<Publisher> registeredPublishers;
    static boolean leak = false;
    static HashMap<String, String> HM = new HashMap<>();
    static ArrayList<String> Topics = new ArrayList<>();
    static int portid;
    static String myIP;
    static ServerSocket providerSocket = null;
    static HashMap<String, String> IPPORT;

    public static void main(String[] args) {
        System.out.println("Which broker is this? Type 1 for first, 2 for second & 3 for third: ");
        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
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
        System.out.println("Broker with port " + portid + " is opening...");
        while (true) {
            Socket connection;
            try {
                connection = providerSocket.accept();
                //System.out.println("New Connection");
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                //System.out.println("pasda");
                Thread t = new ClientHandler(connection, in, out);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void calculateKeys(int portid) {
        HashMap<String, String> Buslines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        IPPORT = FileReaders.readBusLines(new File("Brokers.txt"));
        //Set<String> keys = Buslines.keySet();
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
        System.out.println("Broker[" + portid + "]: " + Topics);
        IPPORT.remove(portid + "");
    }

    public static synchronized void notify(int port) {
        //System.out.println("TEST" + portid);
        ObjectOutputStream out;
        try {
            Socket innercontact = new Socket(InetAddress.getByName("localhost"), port);
            out = new ObjectOutputStream(innercontact.getOutputStream());
            Message info = new Message(Topics, portid);
            //System.out.println(info.topics);
            out.writeObject(info);
            out.flush();
        } catch (Exception e) {
        }
    }

}