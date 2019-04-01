import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/*
	TODO: read file for buslines, bus locations etc
	TODO: hashing
	Threads
*/

public class Broker {
	
	public static void main(String[] args) {
		new Broker().openServer();
	}
	
	public void openServer() {
		ServerSocket providerSocket = null;
		Socket connection=null;
		// ServerSocket senderSocket = null;
		Message info = null;
		try {
			providerSocket = new ServerSocket(10240);
				while (true) {
					connection = providerSocket.accept();
					
					ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

					Message temp=(Message) in.readUnshared();
					System.out.println(temp);
					out.flush();
					
					if(temp.id==1){
						info=temp;
					}
					if(temp.id==2){
						out.writeUnshared(info);
					}
					
					out.flush();
					in.close();
					out.close();
					connection.close();
				}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}


	List <Broker> brokers;
	List<Publisher> registeredPublishers;
	List<Subscriber> registeredSubscribers;

	HashMap<Topic,Value> HM;

	int IDforthisBroker;
	static int BrokerID=1000;

	Broker() {
		IDforthisBroker=BrokerID;
		BrokerID++;
	}

	public void init(int i) {
		for(int j=0;j<i;j++) {
			brokers.add(new Broker());
		}
	}
	
	public void calculateKeys() {
	
	}
	
	public Publisher acceptConnection(Publisher publisher) {
		registeredPublishers.add(publisher);
		return publisher;
	
	}
	
	public Subscriber acceptConnection(Subscriber subscriber) {
		registeredSubscribers.add(subscriber);
		return subscriber;
	
	}
	
	public void notifyPublisher(String notification) {
		
	}
	
	public void pull(Topic topic) {
		Value Message; // Threads
		for (Subscriber subs : registeredSubscribers) {
			Message = HM.get(topic);
		}
	}
	
}