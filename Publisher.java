import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Publisher {
    static ArrayList<String> PubsDuty = new ArrayList<>();
    static ServerSocket InfoTaker = null;
    private static int portid;
    static ArrayList<Message> allchoices = new ArrayList<>();
    static HashMap<String, String> busLines = new HashMap<>();
    static ArrayList<Message> busPositions = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        busPositions = FileReaders.readBusPositions(new File("busPositionsNew.txt"));
        init(14111);
        getBrokerList();
        new Publisher().startClient();
    }

    public static void getBrokerList() {
        Socket requestSocket;
        ObjectOutputStream out;
        try {
            requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject(new Message(4, "Give to the publisher all the info ", 14111 + ""));
            out.flush();
            Thread.sleep(50);
        } catch (Exception e) {
        }
        Socket s;
        int i = 0;
        while (true) {
            try {
                s = InfoTaker.accept();
                ObjectInputStream input = new ObjectInputStream(s.getInputStream());
                Message temp = (Message) input.readObject();
                Message info = new Message(temp.topics, temp.port);
                allchoices.add(info);

                i++;
                if (i == 3) break;
            } catch (Exception e) {
                System.err.println("ERROR!");
                continue;
            }
        }
    }

    public void startClient() throws InterruptedException {
        Socket requestSocket;
        ObjectOutputStream out;
        while (true) {
            for (int i = 0; i < busPositions.size(); i++) {
                for (int j = 0; j < PubsDuty.size(); j++) {
                    if (busLines.get(PubsDuty.get(j)).equals(busPositions.get(i).getbusline())) {
                        try {
                            requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
                            out = new ObjectOutputStream(requestSocket.getOutputStream());
                            out.writeObject(new Message(1, PubsDuty.get(j), busPositions.get(i).getbusline() + " - " + busPositions.get(i).getData()));
                            out.flush();
                        } catch (Exception e) {
                            System.out.println("Publisher couldn't connect with Server. Retrying...");
                            Thread.sleep(2000);
                            continue;
                        }
                    }
                }
            }
        }
    }

    public static void init(int i) {

        busLines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        System.out.println("Which Publisher is it (1 or 2)");
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        portid = i;
        int flag = 0;
        if (choice == 1) {
            for (String key : busLines.keySet()) {
                PubsDuty.add(key);
                flag++;
                if (flag == (int) (busLines.size() / 2)) {
                    break;
                }
            }
            System.out.println(PubsDuty);
        } else {
            for (String key : busLines.keySet()) {
                flag++;
                if (flag > (int) (busLines.size() / 2)) {
                    PubsDuty.add(key);

                }
            }
            System.out.println(PubsDuty);
        }

        try {
            InfoTaker = new ServerSocket(i + (choice - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*

    public Broker hashTopic(Topic topic) {
    }

    public void push(Topic topic, Value value) {
    }

    public void notifyFailure(Broker broker) {
    }

    public void connect() {
    }

    public void disconnect() {
    }

     */

}