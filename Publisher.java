import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Publisher {
    static ServerSocket InfoTaker = null;
    private static int portid;
    Broker brok;
    static ArrayList<Message> allchoices = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        init(14111);
        Publisher p = new Publisher();
        p.getBrokerList();
        new Publisher().startClient(); // p.startClient();
    }

    public void getBrokerList() {
        Socket requestSocket;
        ObjectOutputStream out;
        try {
            requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject(new Message(4, "Give all the information to the publisher. ", 14111 + ""));
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
                System.out.println(temp.port);
                Message info = new Message(temp.topics, temp.port);
                allchoices.add(temp);
                System.out.println(allchoices.get(i));
                System.out.println(info);
                i++;
                if (i == 3) break;
            } catch (Exception e) {
                System.err.println("ERROR!");
                continue;
            }
        }
        System.out.println(allchoices);
    }

    public void startClient() throws InterruptedException {
        Socket requestSocket;
        ObjectOutputStream out;
        HashMap<String, String> busLines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        ArrayList<Message> busPositions = FileReaders.readBusPositions(new File("busPositionsNew.txt"));
        while (true) {
            for (int i = 0; i < busPositions.size(); i++) {
                for (String key : busLines.keySet()) {
                    if (busLines.get(key).equals(busPositions.get(i).getbusline())) {
                        try {
                            requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
                            out = new ObjectOutputStream(requestSocket.getOutputStream());
                            out.writeObject(new Message(1, key, busPositions.get(i).getbusline() + " - " + busPositions.get(i).getData()));
                            out.flush();
                            Thread.sleep(50);
                        } catch (Exception e) {
                            System.out.println("Publisher couldn't connect with Server! Retrying in 3.. 2.. 1..");
                            Thread.sleep(3000);
                            continue;
                        }
                    }
                }
            }
        }

    }

    public static void init(int i) {
        Publisher.portid = i;
        try {
            InfoTaker = new ServerSocket(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public void push(Topic topic, Value value) {}
    public void notifyFailure(Broker broker) {}
    public void connect() {}
    public void disconnect() {}
    */
}