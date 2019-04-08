import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {

    private static final long serialVersionUID = 12345L;
    ArrayList<String> topics;
    int PubSubBrok, port;
    String busline, data;
    HashMap<String,String> ports=new HashMap<>();

    public Message(int PubSubBrok, String busline, String data) {
        super();
        this.PubSubBrok = PubSubBrok;
        this.busline = busline;
        this.data = data;
    }

    public Message(ArrayList<String> topics, int port) {
        this.topics = topics;
        this.port = port;
    }
    public Message(ArrayList<String> topics, HashMap<String,String> ports) {
        this.topics = topics;
        this.ports = ports;
    }


    public int getPubSub() {
        return PubSubBrok;
    }

    public String getbusline() {
        return busline;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return busline + " - " + data;
    }

}