import java.io.ObjectOutputStream;
import java.net.Socket;

class PubHandler extends Thread {
    final ObjectOutputStream out;
    final Socket s;
    final int port;

    /**
     * @param s    Socket used for the communication
     * @param out  Output stream used for passing the messages.
     * @param port This is the port number that belongs to the Broker we want to connect to.
     */
    public PubHandler(Socket s, ObjectOutputStream out, int port) {
        this.s = s;
        this.out = out;
        this.port = port;
        System.out.println("===================================================================");
    }

    /**
     * This method is used to push messages from the Publisher to the Broker.
     */
    public void run() {
        while (true) {
            System.out.println("============asdasdasdasda===================");
            try {//TODO should pass information regardless the route . see last updates on hashtables if(Publisher.PubsDuty.get(buslines.get(i).lineCode)=....
                for (int i = 0; i < Publisher.allchoices.size(); i++) {
                    if (port == Publisher.allchoices.get(i).port) {
                        for (int j = 0; j < Publisher.busPositions.size(); j++) {
                            for (int x = 0; x < Publisher.allchoices.get(i).topics.size(); x++) {
                                if ((Publisher.busLines.get(Publisher.busPositions.get(j).lineCode).equals((Publisher.allchoices.get(i).topics.get(x))) && Publisher.PubsDuty.contains(Publisher.allchoices.get(i).topics.get(x)))) {
                                    Message msg = (new Message("FromHandlerToPush", Publisher.allchoices.get(i).topics.get(x),Publisher.busPositions.get(j).lineCode,Publisher.busPositions.get(j).RouteCode,Publisher.busPositions.get(j).Vehicle ,Publisher.busPositions.get(j).data));
                                    try {
                                        Publisher.push(out, msg);
                                    } catch (Exception e) {
                                        Publisher.notifyFailure(out);
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