import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * All constructors differentiate from each other in order to maximize usability.
 */

public class Message implements Serializable {
    private static final long serialVersionUID = 12345L;
    HashMap<String, String> ports = new HashMap<>();
    ArrayList<String> topics;
    String busline, data, PubSubBrok;
    int port;
	String ip;

    public Message(String PubSubBrok, String busline, String data) {
        super();
        this.PubSubBrok = PubSubBrok;
        this.busline = busline;
        this.data = data;
    }
    public Message(String PubSubBrok, String busline, String data,String vehicleID,String LineCode,String RoutCode){
        super();
        this.PubSubBrok = PubSubBrok;
        this.busline = busline;
        this.data = data;
    }
	
	public Message(ArrayList<String> topics,String IP ,int port) {
        this.topics = topics;
        this.port = port;
		this.ip=IP;
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

    /**
     * This method returns the bus line and the current location.
     * Overrides toString().
     *
     * @return Bus Line plus longitude, latitude.
     */
    public String toString() {
        return busline + ": " + data;
    }
}