import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Broker {
    static HashMap<String, String> HM = new HashMap<>();
    static HashMap<String, String> IPPORT;
    static ArrayList<String> Topics = new ArrayList<>();
    static ServerSocket providerSocket = null;
    static String myIP;
    static boolean leak = false;
    static int portid;

    public static void main(String[] args) {
        IPPORT = FileReaders.readBusLines(new File("Brokers.txt"));
        System.out.println("Which broker is this? Type 1 for first, 2 for second & 3 for third: ");
        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        myIP = IPPORT.get(choice + "");
        init();
        calculateKeys(portid);
        acceptConnections();
    }

    /**
     * This method opens the Broker's server.
     */
    public static void init() {
        try {
            providerSocket = new ServerSocket(portid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method accepts connections and creates threads used for the concurrent
     * communication between the Broker and the Subscribers.
     */
    public static void acceptConnections() {
        System.out.println("Broker with port " + portid + " is opening...");
        while (true) {
            Socket connection;
            try {
                connection = providerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                Thread t = new ClientHandler(connection, in, out);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method hashes a particular Broker's IP+PORT, plus all the bus line IDs using
     * the MD5 algorithm.
     * It also assigns buses whose hash digest is lower than a Broker's IP+PORT hash to that
     * particular Broker.
     *
     * @param portid This is the PORT number of the Broker whose IP+PORT we want to hash.
     */
    public static void calculateKeys(int portid) {
        HashMap<String, String> Buslines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        HashMap<String, BigInteger> digestsofPort = new HashMap<>();
        BigInteger max = BigInteger.ZERO;
        for (String key : IPPORT.keySet()) {
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

    /**
     * This method is used to notify others of which buses a particular Broker is responsible for.
     *
     * @param port This is the PORT of the Broker for whom we want to show information.
     */
    public static synchronized void notify(String ip,int port) {
        ObjectOutputStream out;
        try {
            Socket innercontact = new Socket(ip, port);
            out = new ObjectOutputStream(innercontact.getOutputStream());
            Message info = new Message(Topics, portid);
            out.writeObject(info);
            out.flush();
        } catch (Exception e) {
        }
    }
}