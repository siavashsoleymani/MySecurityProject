import javax.crypto.SecretKey;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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
                    dataOutputStream.writeUTF(EncryptUtil.encrypt(Protocol.SENDING_NEW_KEY, secretKey));
                    secretKey = CustomKeyGenerator.generateRandomBasedKey();
                    oos.writeObject(secretKey);
                } else if (line.equalsIgnoreCase(Protocol.SEND_ME_NEW_FILE)) {
                    File file = new File("./ss.txt");
                    FileChunk.splitFile(file);
                    List<File> chunkFiles = FileChunk.listOfFilesToMerge(new File("./ss.txt.001"));
                    for (File chunkFile : chunkFiles) {
                        EncryptUtil.encryptFile(chunkFile, secretKey);
                        chunkFile = new File("enc_" + chunkFile.getName());
                        dataOutputStream.writeUTF(EncryptUtil.encrypt(Protocol.SENDING_NEW_FILE + "-" + chunkFile.length() + "-" + chunkFile.getName(), secretKey));
                        byte[] mybytearray = new byte[(int) chunkFile.length()];
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(chunkFile));
                        bis.read(mybytearray, 0, mybytearray.length);
                        os.write(mybytearray, 0, mybytearray.length);
                        os.flush();
                    }
                    dataOutputStream.writeUTF(EncryptUtil.encrypt(Protocol.EOF+"-"+"enc_" + file.getName(), secretKey));

                } else {
                    dataOutputStream.writeUTF(EncryptUtil.encrypt(Protocol.OK, secretKey));
                }
            }
        }
    }
}