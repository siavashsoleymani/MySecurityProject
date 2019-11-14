import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CustomKeyGenerator {
    public static SecretKey generateTimeBasedKey() throws Exception{
        return new SecretKeySpec(getTimeBasedPassword(), "AES");
    }

    public static SecretKey generateRandomBasedKey() throws NoSuchAlgorithmException {
        SecureRandom rand = new SecureRandom();
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256, rand);
        return generator.generateKey();
    }
    private static byte[] getTimeBasedPassword() {
        StringBuilder timeBasedPassword = new StringBuilder(System.currentTimeMillis() / (10000l) + "");
        for (int i = 0; i < 22 - timeBasedPassword.length(); i++) {
            timeBasedPassword.append("0");
        }
        return timeBasedPassword.toString().getBytes();
    }
}
