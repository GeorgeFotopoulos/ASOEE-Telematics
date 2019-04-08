import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Subscriber {

    static HashMap<String, ArrayList<String>> TopicsAndPorts = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        NotifyClient();
        Scanner in = new Scanner(System.in);
        System.out.println("Choose one of the following Lines you want to get Info");
        HashMap<String, String> Buslines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        for (String key : Buslines.keySet())
            System.out.print(key + "   ");
        System.out.println();
        String choice = in.next();
        System.out.println(choice);
        getInfo(choice);
    }

    Message current = new Message("", "asdasd ", "asda");

    public static void NotifyClient() {
        Socket subSocket;
        ObjectOutputStream out;
        ObjectInputStream in;
        try {
            subSocket = new Socket(InetAddress.getByName("localhost"), 10256);
            out = new ObjectOutputStream(subSocket.getOutputStream());
            out.writeObject(new Message("NotifySub", "", ""));
            out.flush();
            in = new ObjectInputStream(subSocket.getInputStream());
            Message temp = (Message) in.readObject();
            TopicsAndPorts.put(10240 + "", temp.topics);
            subSocket.close();
            for (String key : temp.ports.keySet()) {
                subSocket = new Socket(InetAddress.getByName("localhost"), Integer.parseInt(key));
                out = new ObjectOutputStream(subSocket.getOutputStream());
                in = new ObjectInputStream(subSocket.getInputStream());
                out.writeObject(new Message("NotifySub", "", ""));
                out.flush();
                Message info = (Message) in.readObject();
                TopicsAndPorts.put(key, info.topics);
                subSocket.close();
            }
            for (String key : TopicsAndPorts.keySet()) {
                System.out.println(key + " " + TopicsAndPorts.get(key));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void getInfo(String choice) {
        int brokerPort;
        for(String keys : TopicsAndPorts.keySet()){
            if(TopicsAndPorts.get(keys).contains(choice)) {
                brokerPort = Integer.parseInt(keys);
            }
        }
    }

    /*

    public void register(Broker broker, Topic topic) {
        broker.acceptConnection(this);
        broker.HM.get(topic);
    }

    public void disconnect(Broker broker, Topic topic) {
        broker.registeredSubscribers.remove(topic);
    }

    public void visualiseData(Topic topic, Value value) {
    }

    public static void init(int i) {
    }

    public void connect() {
    }

    public void disconnect() {
    }

     */
}