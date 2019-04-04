import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

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
		HashMap<String, String> busLines = FileReaders.readBusLines(new File("busLinesNew.txt"));
		ArrayList<Message> busPositions = FileReaders.readBusPositions(new File("busPositionsNew.txt"));
		try {
			while (true) {
			    for(int i=0; i < busPositions.size(); i++) {
			    	
					if(busPositions.get(i).getbusline().equals("1219")){
						requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
						out = new ObjectOutputStream(requestSocket.getOutputStream());
						in = new ObjectInputStream(requestSocket.getInputStream());
						out.writeObject(new Message(true, busLines.get(busPositions.get(i).getbusline()),busPositions.get(i).getbusline()+" - " +busPositions.get(i).getData()));
						 Thread.sleep(1500);
						 out.flush();
					}
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
	
	public void notifyFailure(Broker broker) {}
	
}