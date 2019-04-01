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
	
	public void startClient() throws ClassNotFoundException, InterruptedException {
		Socket subSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String message;
		try {
			
			while (true) {
				//wait(2000);
				subSocket = new Socket(InetAddress.getByName("localhost"), 10240);
				Thread.sleep(2000);
				out = new ObjectOutputStream(subSocket.getOutputStream());
				in = new ObjectInputStream(subSocket.getInputStream());
				
				//out.writeUnshared(new Message(101, "test data")); // Stelnei antikeimena, kaluterh apo writeObject
				out.writeObject(new Message(2, "test Subscriber data")); // Stelnei antikeimena
				out.flush();
				
				
				
				System.out.println((Message) in.readUnshared());
				out.flush();
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
	
	public void visualiseData(Topic topic, Value value) {
	
	}
}