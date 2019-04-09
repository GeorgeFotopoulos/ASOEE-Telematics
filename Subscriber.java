import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Subscriber {
    static HashMap<String, ArrayList<String>> TopicsAndPorts = new HashMap<>();
    static Socket subSocket;
    static ObjectOutputStream out;
    static ObjectInputStream in;
    static String choice;
    private static final String IPOFSUB = "192.168.1.4";
    private static int PORTTOSEND;


    public static void main(String[] args) {
        HashMap<String, String> IPPORT = FileReaders.readBusLines(new File("Brokers.txt"));
        for (String key : IPPORT.keySet()) {
            PORTTOSEND = Integer.parseInt(key);
            break;
        }
        IPPORT.clear();
        notifyClient();
        Scanner input = new Scanner(System.in);
        System.out.println("Choose one of the following bus lines to get its position information: ");
        HashMap<String, String> Buslines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        for (String key : Buslines.keySet()) {
            System.out.print(key + " ");
        }
        System.out.println();
        choice = input.next();
        getInfo(choice);
    }

    /**
     * In this method, we set up the socket, input and output streams which will be used during the
     * communication between the Subscriber and the Brokers.
     * <p>
     * At first the Subscriber opens communication with the first Broker and gets all the information
     * regarding that Broker.
     * <p>
     * After that is done, the Subscriber opens new sockets, input and output streams and begins communicating
     * with the rest of the Brokers to get all the information about which buses they are responsible for.
     */
    public static void notifyClient() {
        try {
            subSocket = new Socket(IPOFSUB, PORTTOSEND);
            out = new ObjectOutputStream(subSocket.getOutputStream());
            out.writeObject(new Message("NotifySub", "", ""));
            out.flush();
            in = new ObjectInputStream(subSocket.getInputStream());
            Message temp = (Message) in.readObject();
            TopicsAndPorts.put(PORTTOSEND + "", temp.topics);
            subSocket.close();
            for (String key : temp.ports.keySet()) {
                subSocket = new Socket(IPOFSUB, Integer.parseInt(key));
                out = new ObjectOutputStream(subSocket.getOutputStream());
                in = new ObjectInputStream(subSocket.getInputStream());
                out.writeObject(new Message("NotifySub", "", ""));
                out.flush();
                Message info = (Message) in.readObject();
                TopicsAndPorts.put(key, info.topics);
                subSocket.close();
            }
            for (String key : TopicsAndPorts.keySet()) {
                System.out.println("Broker[" + key + "]: " + TopicsAndPorts.get(key));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                subSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method gets the client's choice of bus and then finds which Broker is responsible for
     * this particular bus.
     * <p>
     * If the subscriber's choice belongs to some Broker, then a new socket, input and output stream are
     * created and through them starts the communication between the Broker and the Subscriber which
     * leads to the Subscriber getting all the information related to that particular bus's position.
     * <p>
     * If there is no such bus, then the client is informed and is asked to choose all over again.
     *
     * @param choice Subscriber's choice of bus, for which he wants to get information
     */
    public static void getInfo(String choice) {
        int brokerPort = 0;
        for (String keys : TopicsAndPorts.keySet()) {
            if (TopicsAndPorts.get(keys).contains(choice)) {
                brokerPort = Integer.parseInt(keys);
                break;
            }
        }
        try {
            if (brokerPort != 0) {
                subSocket = new Socket(IPOFSUB, brokerPort);
                out = new ObjectOutputStream(subSocket.getOutputStream());
                in = new ObjectInputStream(subSocket.getInputStream());
                out.writeObject(new Message("InfoToSub", choice, ""));
                while (true) {
                    Message info = (Message) in.readObject();
                    System.out.println(info);
                }
            } else {
                System.out.println("There is no Bus with code " + choice);
                Subscriber.main(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}