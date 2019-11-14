import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptUtil {
    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";
    private static Cipher cipher;

    public static String encrypt(String value, SecretKey secretKey) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encrypted, SecretKey secretKey) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(original);
    }

    public static void encryptFile(File file, SecretKey secretKey) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String content = new String(data, StandardCharsets.UTF_8);

        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] ivByte = cipher.getIV();

        try (FileOutputStream fileOut = new FileOutputStream("enc-"+file.getName());
             CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {
            fileOut.write(ivByte);
            cipherOut.write(content.getBytes());
        }
    }

    public static String decryptFile(String fileName, String outPutFile, SecretKey secretKey) throws Exception {
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        String content;

        try (FileInputStream fileIn = new FileInputStream(fileName)) {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            fileIn.read(iv.getIV());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            try (
                    CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
                    InputStreamReader inputReader = new InputStreamReader(cipherIn);
                    BufferedReader reader = new BufferedReader(inputReader)
            ) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                content = sb.toString();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(new File(outPutFile));
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        }
        return content;
    }
}
