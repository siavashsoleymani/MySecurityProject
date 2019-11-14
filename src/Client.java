import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        SecretKey secretKey = CustomKeyGenerator.generateTimeBasedKey();

        try (Socket socket = new Socket((String) null, Constants.PORT);
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             DataInputStream userInput = new DataInputStream(System.in);
             DataInputStream socketInput = new DataInputStream(socket.getInputStream());
             InputStream socketInputStream = socket.getInputStream();
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            String line = "";
            while (!Protocol.SHUTDOWN_MSG.equals(line)) {
                line = userInput.readLine();
                out.writeUTF(EncryptUtil.encrypt(line, secretKey));
                String commandFromNetwork = socketInput.readUTF();
                commandFromNetwork = EncryptUtil.decrypt(commandFromNetwork, secretKey);
                if (commandFromNetwork.equalsIgnoreCase(Protocol.SENDING_NEW_KEY)) {
                    Object newKey = ois.readObject();
                    secretKey = (SecretKey) newKey;
                } else if (commandFromNetwork.startsWith(Protocol.SENDING_NEW_FILE)) {
                    String[] split = commandFromNetwork.split("-");
                    int fileSize = Integer.parseInt(split[1]);
                    String fileName = "rcv_" + split[2];
                    byte[] mybytearray = new byte[fileSize];
                    FileOutputStream fos = new FileOutputStream(fileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int bytesRead = socketInputStream.read(mybytearray, 0, mybytearray.length);
                    bos.write(mybytearray, 0, bytesRead);
                    bos.close();
                    try {
                        EncryptUtil.decryptFile(fileName, "dec-" + fileName, secretKey);
                    } catch (Exception e) {
                    }
                    out.writeUTF(EncryptUtil.encrypt(Protocol.OK, secretKey));
                }
            }
        }
    }
}
