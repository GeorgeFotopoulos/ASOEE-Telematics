import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Publisher {
    static ServerSocket InfoTaker = null;
    private static int portid;
    Broker brok;
    static ArrayList<Message> allchoices = new ArrayList<>();
    static HashMap<String, String> busLines = new HashMap<>();
    static ArrayList<Message> busPositions = new ArrayList<>();

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        busPositions = FileReaders.readBusPositions(new File("busPositionsNew.txt"));
        init(14111);
        getBrokerList();
        System.out.println("Gemise");
        new Publisher().startClient();
    }

    public static void getBrokerList() {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject(new Message(4, "Give to the publisher all the info ", 14111 + ""));
            Thread.sleep(50);
            out.flush();
        } catch (Exception e) {
        }
        Socket s;
        int i = 0;
        while (true) {
            try {
                // socket object to receive incoming client requests
                s = InfoTaker.accept();

                // obtaining input and out streams

                ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
                ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
                Message temp = (Message) dis.readObject();
                dos.flush();
                Message info = new Message(temp.topics, temp.port);
                allchoices.add(info);
                System.out.println(allchoices.get(i));
                i++;
                if (i == 3)
                    break;
                // create a new thread object
                //  Thread t = new ClientHandler(s, dis, dos);

                // Invoking the start() method
                //t.start();

            } catch (Exception e) {
                System.out.println("errrorroroorro");
                continue;
            }
        }
        // System.out.println(allchoices);
    }

    public void startClient() throws ClassNotFoundException, InterruptedException {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        while (true) {
            for (int i = 0; i < busPositions.size(); i++) {
                for (String key : busLines.keySet()) {
                    //System.out.println(busLines.get(key));
                    //System.out.println(busPositions.get(i).getbusline());
                    if (busLines.get(key).equals(busPositions.get(i).getbusline())) {
                        try {
                            requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
                            out = new ObjectOutputStream(requestSocket.getOutputStream());
                            in = new ObjectInputStream(requestSocket.getInputStream());
                            out.writeObject(new Message(1, key, busPositions.get(i).getbusline() + " - " + busPositions.get(i).getData()));
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
        Publisher.portid = i;
        try {
            InfoTaker = new ServerSocket(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void push(Topic topic, Value value) {

    }

    public void notifyFailure(Broker broker) {
    }

}