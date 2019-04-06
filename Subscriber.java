import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
                out.writeObject(new Message(2, "035", " sub inquiry"));
                out.flush();
                Message temp = (Message) in.readObject();
                if (!current.data.equals(temp.data)) {
                    current.data = temp.data;
                    System.out.println(temp);
                }
            } catch (Exception e) {
                System.out.println("Connection with server is not ready yet ");
                Thread.sleep(2000);
                continue;
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