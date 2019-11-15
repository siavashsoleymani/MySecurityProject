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
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(String encrypted, SecretKey secretKey) throws Exception {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
    }

    public static void encryptFile(File file, SecretKey secretKey) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String content = new String(data, StandardCharsets.UTF_8);

        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        byte[] ivByte = cipher.getIV();

        try (FileOutputStream fileOut = new FileOutputStream("enc_" + file.getName());
             CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {
            fileOut.write(ivByte);
            cipherOut.write(content.getBytes());
        }
    }

    public static String decryptFile(String fileName, String outPutFile, SecretKey secretKey) throws Exception {
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        String content;

        try (FileInputStream fileIn = new FileInputStream(fileName)) {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            fileIn.read(ivspec.getIV());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

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
