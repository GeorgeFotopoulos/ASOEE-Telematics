import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Subscriber {

    public static void main(String[] args) throws InterruptedException {
        new Subscriber().startClient();
    }

    Message current = new Message(2, "asdasd ", "asda");

    public void startClient() throws InterruptedException {
        Socket subSocket;
        ObjectOutputStream out;
        ObjectInputStream in;
        while (true) {
            try {
                subSocket = new Socket(InetAddress.getByName("localhost"), 10240);
                out = new ObjectOutputStream(subSocket.getOutputStream());
                in = new ObjectInputStream(subSocket.getInputStream());
                out.writeObject(new Message(2, "036", " sub inquiry"));
                out.flush();
                Message temp = (Message) in.readObject();
                if (!current.data.equals(temp.data)) {
                    current.data = temp.data;
                    System.out.println(temp);
                }
            } catch (Exception e) {
                System.out.println("Subscriber couldn't connect with Server! Retrying in 3.. 2.. 1..");
                Thread.sleep(3000);
                continue;
            }
        }
    }

    /*
    public void register(Broker broker, Topic topic) {
        broker.acceptConnection(this);
        broker.HM.get(topic);
    }
    public void disconnect(Broker broker, Topic topic) { broker.registeredSubscribers.remove(topic); }
    public void visualiseData(Topic topic, Value value) {}
    public void connect() {}
    public void disconnect() {}
    public static void init(int i) {}
    */
}