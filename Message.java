import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private static final long serialVersionUID = 12345L;
    ArrayList<String> topics;
    int PubSubBrok, port;
    String busline, data;

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

    public int getPubSub() {
        return PubSubBrok;
    }

    public void setPubSub(int PubSubBrok) {
        this.PubSubBrok = PubSubBrok;
    }

    public String getbusline() {
        return busline;
    }

    public void setbusline(String busline) {
        this.busline = busline;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String toString() {
        return busline + " - " + data;
    }

}