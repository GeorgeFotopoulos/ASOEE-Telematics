import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    private static final long serialVersionUID = 12345L;
    ArrayList<String> topics;
    int port;
    String busline, data, PubSubBrok;
    HashMap<String, String> ports = new HashMap<>();

    public Message(String PubSubBrok, String busline, String data) {
        super();
        this.PubSubBrok = PubSubBrok;
        this.busline = busline;
        this.data = data;
    }

    public Message(ArrayList<String> topics, int port) {
        this.topics = topics;
        this.port = port;
    }

    public Message(ArrayList<String> topics, HashMap<String, String> ports) {
        this.topics = topics;
        this.ports = ports;
    }

    public String getPubSub() {
        return PubSubBrok;
    }

    public String toString() {
        return busline + ": " + data;
    }
}