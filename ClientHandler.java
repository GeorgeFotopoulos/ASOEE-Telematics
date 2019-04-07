import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

class ClientHandler extends Thread
{
    final ObjectInputStream dis;
    final ObjectOutputStream dos;
    final Socket s;


    // Constructor 
    public ClientHandler(Socket s, ObjectInputStream dis, ObjectOutputStream dos)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run()
    {

        String toreturn;
        Message received = null;
        try {
            received = (Message) dis.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        while (true)
        {
            try {
                if(received.getPubSub() == 1){
                    while(true){
                        received = (Message) dis.readObject();
                        Broker.HM.put(received.busline,received.data);
                        System.out.println("busline : "+ received.busline+" data: "+received.data+" Broker"+Broker.portid);
                    }
                } else if (received.getPubSub() == 3) {
                   // System.out.println("PHRE KWDIKO 3");
                   // System.out.println(received.busline);
                    Broker.notify(Integer.parseInt(received.busline));
                    break;
                } else if (received.getPubSub() == 4) {
                   // System.out.println(received);
                   // System.out.println("TYPOU 4");
                    for (String key : Broker.IPPORT.keySet()) {
                        try {
                            Socket innercontact = new Socket(InetAddress.getByName("localhost"), Integer.parseInt(key));
                            ObjectOutputStream dis = new ObjectOutputStream(innercontact.getOutputStream());
                            dis.writeObject(new Message(3, received.data, " Should send the topics to this port"));
                            dis.flush();
                        } catch (Exception e) {
                        }

                    }
                    Broker.notify(Integer.parseInt(received.data));
                    break;
                }
                //System.out.println("kleinei to connection");
                s.close();

                // Ask user what he wants 
              //  dos.writeObject("What do you want?[Date | Time]..\n"+
              //          "Type Exit to terminate connection.");

                // receive the answer from client 


            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // closing resources 
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}