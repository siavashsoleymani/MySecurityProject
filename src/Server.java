import javax.crypto.SecretKey;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        SecretKey secretKey = CustomKeyGenerator.generateTimeBasedKey();

        try (ServerSocket serverSocket = new ServerSocket(Constants.PORT);
             Socket socket = serverSocket.accept();
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             OutputStream os = socket.getOutputStream();
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {
            String line = "";
            while (!Protocol.SHUTDOWN_MSG.equals(line)) {
                line = dataInputStream.readUTF();
                line = EncryptUtil.decrypt(line, secretKey);
                System.out.println(line);
                if (line.equalsIgnoreCase(Protocol.GENERATE_KEY_MSG)) {
                    secretKey = CustomKeyGenerator.generateRandomBasedKey();
                    dataOutputStream.writeUTF(EncryptUtil.encrypt(Protocol.SENDING_NEW_KEY, secretKey));
                    oos.writeObject(secretKey);
                } else if (line.equalsIgnoreCase(Protocol.SEND_ME_NEW_FILE)) {
                    File file = new File("ss.txt");
                    EncryptUtil.encryptFile(file, secretKey);
                    dataOutputStream.writeUTF(EncryptUtil.encrypt(Protocol.SENDING_NEW_FILE + "-" + file.length() + "-" + file.getName(), secretKey));
                    byte[] mybytearray = new byte[(int) file.length()];
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(mybytearray, 0, mybytearray.length);
                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();
                } else {
                    dataOutputStream.writeUTF(EncryptUtil.encrypt(Protocol.OK, secretKey));
                }
            }
        }
    }
}