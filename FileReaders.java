import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class FileReaders {

    public static HashMap<String, String> readBusLines(File fileName) {
        HashMap<String, String> busLines = new HashMap<>();
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(sCurrentLine, ",");
                String lineCode = st.nextToken();
                String lineID = st.nextToken();
                busLines.put(lineID, lineCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return busLines;
    }

    //public static void readRouteCodes(File fileName) {}

    public static ArrayList<Message> readBusPositions(File fileName) {
        ArrayList<Message> busPositions = new ArrayList<>();
        BufferedReader br = null;
        FileReader fr = null;
        String latitude, longitude;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(sCurrentLine, ",");
                String lineCode = st.nextToken();
                st.nextToken();
                st.nextToken();
                latitude = st.nextToken();
                longitude = st.nextToken();
                busPositions.add(new Message(1, lineCode, latitude + " " + longitude));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return busPositions;
    }

}