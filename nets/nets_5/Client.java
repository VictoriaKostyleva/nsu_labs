import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Client {
    private static final int NUM_CODES = 4;
    private static final char[] CODES = {'A', 'C', 'G', 'T'};
    private static final int TIME_TO_PRINT = 10;
    private static final int SLEEP_TIME = 3000;
    private static String serverName;
    private static int serverPort;
    private final UUID uuid;
    private long length;
    private long start;
    private long end;
    private String instruction;
    private boolean resultIsFound;
    private String hash;
    private String result;

    public Client() {
        this.uuid = UUID.randomUUID();
        this.resultIsFound = false;
    }

    public static void main(String[] args) {
        serverName = args[0];
        serverPort = Integer.parseInt(args[1]);
        Client client = new Client();
        client.startWork();
    }

    private void startWork() {
        while (true) {
            if (getInfoFromServer()) {
                if (instruction.equals("WORK")) {
                    lookingForSolution();
                } else {
                    break;
                }
                if (resultIsFound) {
                    try (Socket socket = new Socket(serverName, serverPort);
                         DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

                        dataOutputStream.writeUTF(uuid.toString());
                        dataOutputStream.writeUTF("SUCCESS");
                        dataOutputStream.writeUTF(result);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
            } else {
                try {
                    System.out.println("Server is unreachable");
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                }
            }
        }
    }

    private boolean getInfoFromServer() {
        try (Socket socket = new Socket(serverName, serverPort);
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            dataOutputStream.writeUTF(uuid.toString());
//            System.out.println("Wrote uuid: " + uuid.toString());

            if (hash == null) {
                System.out.println("First connection");
                hash = dataInputStream.readUTF();
                System.out.println("Read hash: " + hash);
            } else {
                dataOutputStream.writeUTF("FAIL");
            }

            instruction = dataInputStream.readUTF();
//            System.out.println("Read instruction: " + instruction);

            if (instruction.equals("WORK")) {
                length = dataInputStream.readLong();
                start = dataInputStream.readLong();
                end = dataInputStream.readLong();
            } else if (instruction.equals("STOPWORK")) {
                System.err.println("Stop finding solution");
            }
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void lookingForSolution() {
        try {
            MessageDigest md5Counter = MessageDigest.getInstance("MD5");
            for (long i = start; i <= end; i++) {
                String currentString = codeToString(i, length);

                if (i % TIME_TO_PRINT == 0) {
                    System.err.println("Current string: " + currentString);
                }

                byte[] tempHash = md5Counter.digest(currentString.getBytes());
//                System.out.println("temp hash: " + tempHash);

                String hexTempHash;
                StringBuilder hexString = new StringBuilder();

                for (byte byteFromTempHash : tempHash) {
                        String hex = Integer.toHexString(0xFF & byteFromTempHash);
                        if (hex.length() == 1) {
                            hexString.append('0');
                        }
                        hexString.append(hex);
//                    System.out.println("hexString: " + hexString);
                    }
                hexTempHash = hexString.toString();

                if (hexTempHash.equals(hash)) {
                    System.err.println("Found solution: " + currentString);
                    result = currentString;
                    resultIsFound = true;
                    return;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }

    private String codeToString(long code, long length) {
        StringBuilder string = new StringBuilder();

        for (long i = 0; i < length; i++) {
            string.append(CODES[(int) (code % NUM_CODES)]);
            code /= NUM_CODES;
        }
        return String.valueOf(string.reverse());
    }
}