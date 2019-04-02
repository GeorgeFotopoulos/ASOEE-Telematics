import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Subscriber {
	
	public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
		new Subscriber().startClient();
	}
	Message current =new Message(true,"asdasd ","asda");
	public void startClient() throws ClassNotFoundException, InterruptedException {
		Socket subSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			while (true) {
				subSocket = new Socket(InetAddress.getByName("localhost"), 10240);
				out = new ObjectOutputStream(subSocket.getOutputStream());
				in = new ObjectInputStream(subSocket.getInputStream());
				
				out.writeObject(new Message(false, "750", " sub inquiry"));
				out.flush();	
				Message temp=(Message) in.readObject();
				if(temp.busline.equals("750") && !current.data.equals(temp.data)){
					current.data=temp.data;
					System.out.println(temp);
					Thread.sleep(2000);
				}
			}
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				subSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void register(Broker broker,Topic topic) {
		broker.acceptConnection(this);
		broker.HM.get(topic);
	}
	
	public void disconnect(Broker broker, Topic topic) {
		broker.registeredSubscribers.remove(topic);
	}
	
	public void visualiseData(Topic topic, Value value) {}
}