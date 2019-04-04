import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class md5 {
    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]) {
        String s1 = "192.168.56.110240";
        String s2 = "192.168.56.110241";
        String s3 = "192.168.56.110242";
        //System.out.println(getMd5(s1).compareTo(getMd5(s2)));
        //System.out.println(getMd5(s1).compareTo(getMd5(s3)));
        //System.out.println(getMd5(s2).compareTo(getMd5(s3)));
        s1 = getMd5(s1);
        BigInteger bg1 = new BigInteger(getMd5(s1), 16);
        System.out.println(bg1);
        BigInteger bg2 = new BigInteger(getMd5(s2), 16);
        System.out.println(bg2);
        BigInteger bg3 = new BigInteger(getMd5(s3), 16);
        System.out.println(bg3);
        // Broker 1 System.out.println((bg1.mod(bg3)));
        // Broker 2 System.out.println((bg2.mod(bg3)));
        // Broker 3 System.out.println((bg3.mod(bg3)));
        //s1 mesaios, s3 megalos, s2 mikros
    }
}