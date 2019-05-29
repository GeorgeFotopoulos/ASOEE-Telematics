import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Publisher {
    private static int portid;
    static ServerSocket InfoTaker = null;
    static ArrayList<Message> allchoices = new ArrayList<>();
    static ArrayList<String> PubsDuty = new ArrayList<>();
    static ArrayList<Message> busPositions = new ArrayList<>();
    static HashMap<String, String> busLines = new HashMap<>();
    private static final String PUBIP = "192.168.1.8";
    private static String IPofBroker;
    private static int PORTTOSEND;
    public static HashMap<String, String> IPPORT;

    public static void main(String[] args) {
        IPPORT = FileReaders.readHash(new File("Brokers.txt"));
        for (String key : IPPORT.keySet()) {
			//System.out.println("- "+ IPPORT);
            PORTTOSEND = Integer.parseInt(key);
			//System.out.println("port: "+PORTTOSEND);
            IPofBroker = IPPORT.get(key);
			//System.out.println("IP of Broker: "+IPofBroker);
        }
        busPositions = FileReaders.readBusPositions(new File("busPositionsNew.txt"));
        System.out.println(busPositions.get(1));
        init();
        getBrokerList();
        new Publisher().startClient();
    }

    /**
     * This method creates a communication between the Publisher and one of the Brokers. Then, the broker informs the
     * Publisher about all the other Brokers and which bus lines each of them is responsible for.
     */
    public static void getBrokerList() {
        Socket requestSocket;
        ObjectOutputStream out;
        try {
            requestSocket = new Socket(IPofBroker, PORTTOSEND);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject(new Message("NotifyPub", PUBIP + "", portid + ""));
            out.flush();
        } catch (Exception e) {
        }
        Socket s;
        int i = 0;
        while (true) {
            try {
                s = InfoTaker.accept();
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                Message temp = (Message) in.readObject();
                Message info = new Message(temp.topics, temp.port);
                allchoices.add(info);
                System.out.println("Broker[" + temp.port + "]: " + temp.topics);
                i++;
                if (i == 3) {
                    in.close();
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * This method is used so that the publisher can inform a broker about a bus's location.
     *
     * @param out Output stream used to pass messages.
     * @param msg Message containing information about a bus line and its location.
     */
    public static synchronized void push(ObjectOutputStream out, Message msg) {
        try {
            out.writeObject(new Message("BusInfoByPub", msg.busline, msg.data));
            out.flush();
        } catch (IOException e) {
            System.err.println("Couldn't send from Pub to Broker");
        }
    }

    /**
     * In this method we create a thread that allows concurrent communication between the Publisher
     * and the Broker that is responsible for a specific bus line.
     */
    public void startClient() {
        Socket requestSocket;
        System.out.println(allchoices.size());
        System.out.println(PubsDuty.size());
        for (int i = 0; i < allchoices.size(); i++) {
            for (int j = 0; j < PubsDuty.size(); j++) {
                if (allchoices.get(i).topics.contains(PubsDuty.get(j))) {
                    try {
                        requestSocket = new Socket(IPPORT.get(allchoices.get(i).port+""), allchoices.get(i).port);
                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        PubHandler ph = new PubHandler(requestSocket, out, allchoices.get(i).port);
                        ph.start();
                        out.flush();
                        break;
                    } catch (Exception e) {
						System.out.println(IPPORT.get(allchoices.get(i)));
                        System.err.println(allchoices.get(i).port);
						e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method creates a Publisher node. At first we're asked to input who this Publisher
     * is going to be (1 or 2), information which will be used to allocate the available bus
     * lines to the Publishers. The first one will get the first nine and the other one will get
     * the remaining nine. Then, we're asked to input the Publisher's Port which will be used
     * to communicate with that particular Publisher.
     * After the bus lines are allocated evenly to the two Publishers, they're printed out on the
     * console for the Subscriber to see.
     * Finally, a socket is created using the Publisher's port, used for communication with the Broker.
     */
    public static void init() {
        busLines = FileReaders.readHash(new File("busLinesNew.txt"));
        System.out.println("Which Publisher is this? Type 1 for first & 2 for second: ");
        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        System.out.println("Give the port of the Publisher");
        portid = input.nextInt();
        int flag = 0;
        if (choice == 1) {
            for (String key : busLines.keySet()) {
                PubsDuty.add(key);
                flag++;
                if (flag == busLines.size() / 2) {
                    break;
                }
            }
            System.out.println("Publisher_1[" + portid + "]: " + PubsDuty);
        } else {
            for (String key : busLines.keySet()) {
                flag++;
                if (flag > busLines.size() / 2) {
                    PubsDuty.add(key);
                }
            }
            System.out.println("Publisher_2[" + portid + "]: " + PubsDuty);
        }
        try {
            InfoTaker = new ServerSocket(portid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to notify the Broker in case the Publisher wasn't able to retrieve information
     * regarding a bus's location.
     *
     * @param out Output stream used to pass message.
     */
    public static void notifyFailure(ObjectOutputStream out) {
        try {
            out.writeObject(new Message("Failure", "A publisher couldn't pass all the info", ""));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}