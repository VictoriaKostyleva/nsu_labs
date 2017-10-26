import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private static final int BUFFER_SIZE = 1024;
    private static final int RESPONSE_SIZE = 8;
    private static String filePath;
    private static String serverName;
    private static int serverPort;

    public static void main(String[] args) {

        serverName = args[0];
        serverPort = Integer.parseInt(args[1]);
        filePath = args[2];

        Client client = new Client();

        try{
        client.startWork();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void startWork() throws IOException {
        try (Socket socket = new Socket(serverName, serverPort);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

            String fileName = filePath;
            File file = new File(fileName);
            long fileSize = file.length();
            System.out.println("File is prepared");
            dataOutputStream.writeLong(fileSize);
            dataOutputStream.writeInt(fileName.length());
            dataOutputStream.writeUTF(fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            int read;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((read = fileInputStream.read(buffer, 0, buffer.length)) > 0) {
                dataOutputStream.write(buffer, 0, read);
                dataOutputStream.flush();
            }
            System.out.println("File was sent");


            byte[] response = new byte[RESPONSE_SIZE];
            dataInputStream.readFully(response);
            String message = new String(response, StandardCharsets.UTF_8);
            System.out.println(message);


            socket.close();

            } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}

