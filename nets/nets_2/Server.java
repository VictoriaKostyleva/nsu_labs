import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import static java.lang.System.exit;

public class Server {
    private static final String DIR_NAME = "./uploads";
    private static int serverPort;

    static String getDirName() {
        return DIR_NAME;
    }

    public static void main(String[] args) {
        serverPort = Integer.parseInt(args[0]);
        Server server = new Server();
        server.startWork();
    }

    private void startWork() {
        System.out.println("Server started work");
        File uploadsDirectory = new File(DIR_NAME);
        if (!uploadsDirectory.exists()) {
            System.out.println("I have made the directory");
            boolean ret = uploadsDirectory.mkdir();
            if(!ret) {
                System.out.println("Error in making a directory");
                exit(1);
            }
        }

        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(serverPort);

            while (!Thread.interrupted()) {
                Socket socket;

                try {
                    socket = serverSocket.accept();
                    System.out.println("New connection: " + socket.getInetAddress().getHostAddress());

                    Thread connectedClient = new Thread(new ConnectedClient(socket));
                    connectedClient.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ConnectedClient implements Runnable {

    private static final int BUFFER_SIZE = 1024;
    private static final int TIME = 3000;

    private Socket socket;

    ConnectedClient(Socket socket) {
        this.socket = socket;
    }

    private int totalRead = 0;

    private long differTime(long t) {
        return Math.abs(System.currentTimeMillis() - t);
    }

    @Override
    public void run() {

        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            long fileSize = dataInputStream.readLong();
            System.out.println("FileSize: " + fileSize);
            int fileNameSize = dataInputStream.readInt();
            System.out.println("FileNameSize: " + fileNameSize);

            int read = 0;
            byte[] stringBuffer = new byte[fileNameSize];

            int readRes = 0;
            readRes = dataInputStream.read(stringBuffer, 0, fileNameSize);
            while (readRes < fileNameSize) {//is like readFully

                if (readRes == -1) {
                    System.out.println("Error in reading");
                    exit(1);
                }
                readRes+= dataInputStream.read(stringBuffer, readRes, fileNameSize - readRes);
            }

            String fileName = new String(stringBuffer, StandardCharsets.UTF_8);

            File tempFile = new File(fileName);
            String name = tempFile.getName();

            System.out.println("Got file " + fileName);
            File file = new File(Server.getDirName() + "/" + fileName);

            try (OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[BUFFER_SIZE];

                long startTime = System.currentTimeMillis();
                long tempTime = startTime;
                long currRead = 0;


                while ((totalRead < fileSize) && ((read = dataInputStream.read(buffer, 0, buffer.length)) != -1)) {

                    currRead += read;//for speed
                    outputStream.write(buffer, 0, read);
                    totalRead += read;
                    outputStream.flush();

                    if(differTime(tempTime) >= TIME) {
                        double speed = (double)currRead / (differTime(tempTime) * 1024 * 1024);
                        System.out.println("Instantaneous speed: " + speed);

                        double averageSpeed = (double)totalRead / (differTime(startTime) * 1024 * 1024);

                        System.out.println("Average speed: " + averageSpeed);
                        currRead = 0;
                        tempTime = System.currentTimeMillis();

                    }
                }

                System.out.println("Average speed2: " + (double)fileSize / (differTime(startTime) * 1024 * 1024));

                String response;

                if (fileSize == totalRead) {
                    response = "Success!";
                    System.out.println("The file was written to the directory");
                } else {
                    response = "Error!";
                    System.out.println("Problem with writing the file");
                }

                dataOutputStream.writeBytes(response);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
