import java.io.ObjectOutputStream;
import java.net.Socket;

class PubHandler extends Thread {
    public Message received;
    final ObjectOutputStream dos;
    final Socket s;
    final int Port;

    // Constructor 
    public PubHandler(Socket s, ObjectOutputStream dos, int Port) {
        this.s = s;
        this.dos = dos;
        this.Port = Port;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (int i = 0; i < Publisher.allchoices.size(); i++) {
                    if (Port == Publisher.allchoices.get(i).port) {
                        for (int j = 0; j < Publisher.busPositions.size(); j++) {
                            //System.out.println(Publisher.allchoices.get(i).topics);
                            for (int x = 0; x < Publisher.allchoices.get(i).topics.size(); x++) {
                                // System.out.println(Publisher.busLines.get(Publisher.allchoices.get(i).topics.get(x)));
                                if ((Publisher.busLines.get(Publisher.allchoices.get(i).topics.get(x))).equals(Publisher.busPositions.get(j).busline)&&Publisher.PubsDuty.contains(Publisher.allchoices.get(i).topics.get(x))) {
                                    Message msg=(new Message("FromHandlerToPush",Publisher.allchoices.get(i).topics.get(x),Publisher.busPositions.get(j).data));
                                    try {
                                        Publisher.push(dos, msg);
                                    }
                                    catch(Exception e){
                                        Publisher.notifyFailure(dos);
                                    }
                                    Thread.sleep(1000);
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                continue;

            }
        }
    }
} 