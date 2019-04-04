import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/*
	TODO: Synchronization, Threads
*/

public class Broker {

	public static void main(String[] args) throws InterruptedException {
		init(10240);
		calculateKeys(10240);
		new Broker().acceptConnections();
	}
	static ArrayList<String> Topics =new ArrayList<>();
	static int portid;
	static String myIP;
	static ServerSocket providerSocket = null;
	static HashMap<String,String> IPPORT;

	public static void init(int i) {
		// TODO: Create arrayList with info of all brokers
		Broker.portid=i;
		try {
			providerSocket = new ServerSocket(portid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void acceptConnections() {
		Message info = null;
		System.out.println("Server with socket " + portid +  " is opening...");
		while (true) {
			Socket connection = null;
			try {
				connection = providerSocket.accept();
				ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
				Message temp=(Message) in.readObject();
				if(temp.getPubSub()){
					info=temp;
				}
				if(!temp.getPubSub()){
					if(info==null){
						out.writeObject(new Message (false, temp.getbusline(), "No connection with the Publisher, retrying..."));
						out.flush();
						Thread.sleep(2000);
					} else {
						if(info.getbusline().equals("036")){
							out.writeObject(info);
							out.flush();
						} else {
							out.writeObject(new Message (false, "Error", ""));
							out.flush();
						}
					}
				}
				connection.close();
			} catch (Exception e) {
				System.err.println("paok");
				continue;
			}
		}
	}

	List<Broker> brokers;
	List<Subscriber> registeredSubscribers;
	HashMap<Topic, Value> HM;

	int IDforthisBroker;
	static int BrokerID=1000;

	public static void calculateKeys(int portid) {
		HashMap<String,String> Buslines=FileReaders.readBusLines(new File("busLinesNew.txt"));
		IPPORT= FileReaders.readBusLines(new File("Brokers.txt"));
		//System.out.println(IPPORT);
		//System.out.println(Buslines);
		Set<String> keys = Buslines.keySet();
		//System.out.println(keys);
		HashMap<String, BigInteger> digestsofPort=new HashMap<>();
		//System.out.println(IPPORT.get(portid+""));
		BigInteger max=BigInteger.ZERO;
		System.out.println(max);
		for(String key: IPPORT.keySet()){
			if(key.equals(portid+""))
				myIP=IPPORT.get(key);
			digestsofPort.put(key, new BigInteger(md5.getMd5(IPPORT.get(key)+key),16));
			if(digestsofPort.get(key).compareTo(max)>0) {
				max = digestsofPort.get(key);
			}
		}
		//System.out.println(max);
		System.out.println(new BigInteger (md5.getMd5(myIP+portid),16));
		for(String key: Buslines.keySet()){
			if(((new BigInteger(md5.getMd5(key),16)).mod(max)).compareTo(new BigInteger(md5.getMd5(myIP+portid),16))<=0 ) {
				Topics.add(key);
				for(String port: digestsofPort.keySet()){
					if (!port.equals(portid+"")){
						if(((new BigInteger(md5.getMd5(key),16)).mod(max)).compareTo(digestsofPort.get(port))<=0&&(new BigInteger(md5.getMd5(myIP+portid),16)).compareTo(digestsofPort.get(port))>0 ){
							Topics.remove(key);
						}
					} else{
						System.out.println("paok");
					}
				}
			}
		}
		IPPORT.remove(portid+"");
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