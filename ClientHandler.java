import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientHandler extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    final Socket s;

    /**
     * @param s   Socket used for the communication.
     * @param in  Input stream used for the communication.
     * @param out Output stream used for the communication.
     */
    public ClientHandler(Socket s, ObjectInputStream in, ObjectOutputStream out) {
        this.s = s;
        this.in = in;
        this.out = out;
    }

    /**
     * Every message coming to a Broker should be of the type public Message(String PubSubBrok, String busline, String data).
     * Depending on the code of PubSubBrok that we get from getPubSub(), we deal with the incoming messages independently,
     * so that requests for messages, notifications, failures are implemented in this run().
     */
    public void run() {
        Message received = null;
        Message toSend;
        String temp;
        ObjectOutputStream output;
        try {
            received = (Message) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                if (received.getPubSub().equals("BusInfoByPub")) {
                    while (true) {
                        received = (Message) in.readObject();
                        Broker.HM.put(received.busline, received.data);
                    }
                } else if (received.getPubSub().equals("InfoToSub")) {
                    if (Broker.leak) {
                        out.writeObject(new Message("Failure to Sub", "ERROR 404: Data Not Found! ", "Due to a problem we couldn't gather info for some BusLines"));
                        out.flush();
                    }
                    temp = "";
                    while (true) {
                        toSend = new Message("Info to Sub", received.busline, Broker.HM.get(received.busline));
                        if (!temp.equals(toSend.data)) {
                            out.writeObject(toSend);
                            out.flush();
                        }
                        temp = toSend.data;
                    }
                } else if (received.getPubSub().equals("InnerBrokerCom")) {
                    Broker.notify(Integer.parseInt(received.busline));
                    break;
                } else if (received.getPubSub().equals("Failure")) {
                    Broker.leak = true;
                } else if (received.getPubSub().equals("NotifySub")) {
                    toSend = new Message(Broker.Topics, Broker.IPPORT);
                    out.writeObject(toSend);
                    out.flush();
                    break;
                } else if (received.getPubSub().equals("NotifyPub")) {
                    for (String key : Broker.IPPORT.keySet()) {
                        try {
                            Socket innercontact = new Socket(Broker.myIP, Integer.parseInt(key));
                            output = new ObjectOutputStream(innercontact.getOutputStream());
                            output.writeObject(new Message("InnerBrokerCom", received.data, " Should send the topics to this port"));
                            output.flush();
                        } catch (Exception e) {
                        }
                    }
                    Broker.notify(Integer.parseInt(received.data));
                    break;
                }
                s.close();
            } catch (Exception e) {
                continue;
            }
        }
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}