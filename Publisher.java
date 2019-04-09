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
    private static final String PUBIP="192.168.1.4";
    private static int PORTTOSEND;

    public static void main(String[] args) {
        HashMap<String, String> IPPORT = FileReaders.readBusLines(new File("Brokers.txt"));
        for (String key : IPPORT.keySet()) {
            PORTTOSEND = Integer.parseInt(key);
            break;
        }
        IPPORT.clear();
        busPositions = FileReaders.readBusPositions(new File("busPositionsNew.txt"));
        init();
        //System.out.println(portid);
        getBrokerList();
        new Publisher().startClient();
    }

    public static void getBrokerList() {
        Socket requestSocket;
        ObjectOutputStream out;
        try {
            requestSocket = new Socket(PUBIP, PORTTOSEND);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject(new Message("NotifyPub", "Give to the publisher all the info ", portid + ""));
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
                //System.out.println("paoapapapap");
                if (i == 3) {
                    in.close();
                    break;
                }
            } catch (Exception e) {
                //System.err.println("ERROR!");
                continue;
            }
        }
    }

    public static synchronized void push(ObjectOutputStream out, Message msg) {
        try {
            out.writeObject(new Message("BusInfoByPub", msg.busline, msg.data));
            out.flush();
        } catch (IOException e) {
            System.err.println("Couldn't send from Pub to Broker");
        }
    }

    public void startClient() {
        Socket requestSocket;
        for (int i = 0; i < allchoices.size(); i++) {
            for (int j = 0; j < PubsDuty.size(); j++) {
                if (allchoices.get(i).topics.contains(PubsDuty.get(j))) {
                    try {
                        requestSocket = new Socket(PUBIP, allchoices.get(i).port);
                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        // in=new ObjectInputStream(requestSocket.getInputStream());
                        PubHandler ph = new PubHandler(requestSocket, out, allchoices.get(i).port);
                        ph.start();
                        out.flush();
                        break;
                    } catch (Exception e) {
                        System.out.println("Error");
                    }
                }
            }
        }
    }

    public static void init() {
        busLines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        System.out.println("Which publisher is this? Type 1 for first & 2 for second: ");
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
            System.out.println("Publisher_1[" + portid +"]: " + PubsDuty);
        } else {
            for (String key : busLines.keySet()) {
                flag++;
                if (flag > busLines.size() / 2) {
                    PubsDuty.add(key);
                }
            }
            System.out.println("Publisher_2[" + portid +"]: " + PubsDuty);
        }

        try {
            InfoTaker = new ServerSocket(portid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void notifyFailure(ObjectOutputStream out) {
        try {
            out.writeObject(new Message("Failure", "A publisher couldn't pass all the info", ""));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}