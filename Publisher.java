import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Publisher {
    private static int portid;
    static ServerSocket InfoTaker = null;
    static ArrayList<Message> allchoices = new ArrayList<>();
    static ArrayList<String> PubsDuty = new ArrayList<>();
    static ArrayList<Message> busPositions = new ArrayList<>();
    static HashMap<String, String> busLines = new HashMap<>();

    public static void main(String[] args) {
        busPositions = FileReaders.readBusPositions(new File("busPositionsNew.txt"));
        init(14111);
        //System.out.println(portid);
        getBrokerList();
        new Publisher().startClient();
    }

    public static void getBrokerList() {
        Socket requestSocket;
        ObjectOutputStream out;
        try {
            requestSocket = new Socket(InetAddress.getByName("localhost"), 10256);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject(new Message("NotifyPub", "Give to the publisher all the info ", portid + ""));
            out.flush();
        } catch (Exception e) {
        }
        Socket s;
        int i = 0;
        while (true) {
            try {
                s = InfoTaker.accept();
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                Message temp = (Message) in.readObject();
                Message info = new Message(temp.topics, temp.port);
                allchoices.add(info);
                System.out.println("Broker[" + temp.port + "]: " + temp.topics);
                i++;
                //System.out.println("paoapapapap");
                if (i == 3) {
                    in.close();
                    break;
                }
            } catch (Exception e) {
                //System.err.println("ERROR!");
                continue;
            }
        }
    }

    public static synchronized void push(ObjectOutputStream out, Message msg) {
        try {
            out.writeObject(new Message("BusInfoByPub", msg.busline, msg.data));
            out.flush();
        } catch (IOException e) {
            System.err.println("Couldn't send from Pub to Broker");
        }
    }

    public void startClient() {
        Socket requestSocket;
        for (int i = 0; i < allchoices.size(); i++) {
            for (int j = 0; j < PubsDuty.size(); j++) {
                if (allchoices.get(i).topics.contains(PubsDuty.get(j))) {
                    try {
                        requestSocket = new Socket(InetAddress.getByName("localhost"), allchoices.get(i).port);
                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        // in=new ObjectInputStream(requestSocket.getInputStream());
                        PubHandler ph = new PubHandler(requestSocket, out, allchoices.get(i).port);
                        ph.start();
                        out.flush();
                        break;
                    } catch (Exception e) {
                        System.out.println("Error");
                    }
                }
            }
        }
    }
//  Socket requestSocket;
//
//  while (true) {
//      for (int i = 0; i < busPositions.size(); i++) {
//          for (int j = 0; j < PubsDuty.size(); j++) {
//              if (busLines.get(PubsDuty.get(j)).equals(busPositions.get(i).getbusline())) {
//                  try {
//                      requestSocket = new Socket(InetAddress.getByName("localhost"), 10240);
//                      out = new ObjectOutputStream(requestSocket.getOutputStream());
//                      out.writeObject(new Message(1, PubsDuty.get(j), busPositions.get(i).getbusline() + " - " + busPositions.get(i).getData()));
//                      out.flush();
//                  } catch (Exception e) {
//                      System.out.println("Publisher couldn't connect with Server. Retrying...");
//                      Thread.sleep(2000);
//                      continue;
//                  }
//              }
//          }
//      }
//  }

    public static void init(int i) {
        busLines = FileReaders.readBusLines(new File("busLinesNew.txt"));
        System.out.println("Which publisher is this? Type 1 for first & 2 for second: ");
        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        portid = i + choice - 1;
        int flag = 0;
        if (choice == 1) {
            for (String key : busLines.keySet()) {
                PubsDuty.add(key);
                flag++;
                if (flag == busLines.size() / 2) {
                    break;
                }
            }
            System.out.println("Publisher_1[" + portid +"]: " + PubsDuty);
        } else {
            for (String key : busLines.keySet()) {
                flag++;
                if (flag > busLines.size() / 2) {
                    PubsDuty.add(key);
                }
            }
            System.out.println("Publisher_2[" + portid +"]: " + PubsDuty);
        }

        try {
            InfoTaker = new ServerSocket(i + (choice - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void notifyFailure(ObjectOutputStream out) {
        try {
            out.writeObject(new Message("Failure", "A publisher couldn't pass all the info", ""));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}