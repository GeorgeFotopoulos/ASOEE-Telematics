import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class md5 {
    /**
     * MD5 algorithm will be used to hash keys, such as Broker's IP+Port and BusLineIDs, so that
     * the bus lines will be somewhat evenly distributed to the Brokers and each one will be
     * responsible for specific keys, whose hash is lower than his IP+Port's hash.
     * @param input The string which we want to hash using MD5 algorithm.
     * @return The outcome of the use of MD5 algorithm on the input String, also known as digest.
     */
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
}