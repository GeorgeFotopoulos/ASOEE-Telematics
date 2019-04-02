import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 12345L;
	boolean pubTrueSubFalse;
	String busline;
	String data;

	public Message(boolean pubTrueSubFalse, String busline, String data) {
		super();
		this.pubTrueSubFalse = pubTrueSubFalse;
		this.busline = busline;
		this.data = data;
	}
	
	public boolean getPubSub() {
		return pubTrueSubFalse;
	}
	
	public void setPubSub(boolean pubTrueSubFalse) {
		this.pubTrueSubFalse = pubTrueSubFalse;
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