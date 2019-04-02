import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/*
	TODO: Hashing, Synchronization, Threads
*/

public class Broker {
	
	public static void main(String[] args) throws InterruptedException {
		new Broker().openServer();
	}
	
	public void openServer() throws InterruptedException {
		ServerSocket providerSocket = null;
		Socket connection=null;
		Message info = null;
		try {
			providerSocket = new ServerSocket(10240);
			System.out.println("Server with socket 10240 is opening...");
				while (true) {
					connection = providerSocket.accept();
					
					ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

					Message temp=(Message) in.readObject();
					System.out.println(temp);
					
					if(temp.getPubSub()){
						info=temp;
					}
					if(!temp.getPubSub()){
						if(info==null){
							try {
								out.writeObject(new Message (false, temp.getbusline(), "No connection with Pub, retrying..."));
								out.flush();
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							if(info.getbusline().equals("750")){
								//Thread.sleep(100);
								out.writeObject(info);
								out.flush();
								//Thread.sleep(2000);
							} else {
								out.writeObject(new Message (false, "Error", ""));
								out.flush();
							}
						}
					}
					
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
	List<Broker> brokers;
	List<Subscriber> registeredSubscribers;
	HashMap<Topic, Value> HM;

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
	
	public Subscriber acceptConnection(Subscriber subscriber) {
		registeredSubscribers.add(subscriber);
		return subscriber;
	}
	
	public void notifyPublisher(String notification) {}
	
	public void pull(Topic topic) {
		Value Message; // Threads
		for (Subscriber subs : registeredSubscribers) {
			Message = HM.get(topic);
		}
	}
	
}