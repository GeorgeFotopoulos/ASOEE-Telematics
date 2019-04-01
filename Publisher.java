import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Publisher {
	Broker brok; 
	static int value = 12313;
	public void getBrokerList() {}
	
	public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
		new Publisher().startClient();
	}
	
	public void startClient() throws ClassNotFoundException, InterruptedException {
		Socket requestSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String message;
		
		try {
			
			while (true) {
				requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
				value++;
				Thread.sleep(2000);
				out = new ObjectOutputStream(requestSocket.getOutputStream());
				in = new ObjectInputStream(requestSocket.getInputStream());
	
				out.writeUnshared(new Message(1,value+"" ));
				
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
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
	
	public Broker hashTopic(Topic topic) {
		return brok;
	}
	
	public void push(Topic topic, Value value) {
		 Broker tempBroker=hashTopic(topic);
		 tempBroker.HM.put(topic, value);
	}
	
	public void notifyFailure(Broker broker) {
		
	}
	//TO DO readfile for buslines, bus locations etc
	//TO DO hashing 
	// threads
		
}