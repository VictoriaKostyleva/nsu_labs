import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.exit;

public class Server {
    private static final int CODES_COUNT = 4;
    private static final int LENGTH = 3;
    private static final long MAX_TIME_DIFF = 10000;
    private static final long SLEEP_TIME = 3000;

    private static int serverPort;
    private static String hashString;
    private Map<String, Task> taskMap;
    private Map<String, Long> timeMap;
    private BlockingQueue<Task> tasks;

    private String result;

    private static boolean workIsDone = false;

    public static void main(String[] args) {
        hashString = args[0];
        serverPort = Integer.parseInt(args[1]);
        Server server = new Server();
        server.startWork();
    }

    private void startWork() {
        try {
//            taskMap = Collections.synchronizedMap(new HashMap<>());
            taskMap = new ConcurrentHashMap<String, Task>();
            timeMap = new ConcurrentHashMap<String, Long>();
            tasks = new ArrayBlockingQueue<>((int) Math.pow(CODES_COUNT, LENGTH));
            makeTasks();
            Thread acceptor = new Thread(new Acceptor());
            acceptor.start();
            Thread timeChecker = new Thread(new TimeChecker());
            timeChecker.start();
            System.out.println("Server started working");


        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
    }


    private void makeTasks() throws InterruptedException {//пояснить как делаются задачки
        int tasksSize = (int) Math.pow(CODES_COUNT, LENGTH);
        long rangeSize = 1;
        int power = 0;
        for (int i = 0; i < tasksSize; i++) {
            if ((i != 0) && (i % CODES_COUNT == 0)) {
                power++;
                rangeSize = (long) Math.pow(CODES_COUNT, power);
            }
            long length = i / CODES_COUNT + 1;
            long start = rangeSize * (i % CODES_COUNT);
            long end = start + rangeSize - 1;
            System.out.println("Task_i:"+ i + " length: "+ length + " start:"+start+ " end:"+end);

            tasks.put(new Task(length, start, end));
        }
    }

    class Acceptor implements Runnable {
        @Override
        public void run() {
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

//                        ConnectedClient connectedClient = new ConnectedClient(socket);
//                        connectedClient.run();//! в отдельном потоке сделать
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class TimeChecker implements Runnable {
        @Override
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    Thread.sleep(SLEEP_TIME);
                    long currentTime = System.currentTimeMillis();
                    for (String key : timeMap.keySet()) {
                        if (timeMap.get(key) - currentTime > MAX_TIME_DIFF) {
                            Task task = taskMap.get(key);
                            tasks.put(task);
                            timeMap.remove(key);//! так нельзя удалять. использовать итератор

                        }
                    }

                    if (workIsDone) {
                        if (timeMap.isEmpty()) {
                            exit(0);
                        }
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class ConnectedClient implements Runnable{
        private final Socket socket;

        ConnectedClient(Socket socket) {
            this.socket = socket;
        }

            @Override
            public void run() {
                try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                     DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

                    String uuidString = dataInputStream.readUTF();
                    System.out.println("Read client uuid: " + uuidString);

                    if(workIsDone) {
                        if(timeMap.containsKey(uuidString)) {
                            timeMap.remove(uuidString);
                        }

                        dataOutputStream.writeUTF("STOPWORK");
                        return;
                    }

                    if (!taskMap.containsKey(uuidString)) {
                        System.out.println("New client");
                        dataOutputStream.writeUTF(hashString);
//                        System.out.println("Wrote hash: " + hashString);
                    } else {
                        System.out.println("Old client");
                        String message = dataInputStream.readUTF();
                        System.out.println("Read message: " + message);
                        if (message.equals("SUCCESS")) {
                            result = dataInputStream.readUTF();
                            System.err.println("Solution: " + result);
                            workIsDone = true;

                            dataOutputStream.writeUTF("STOPWORK");
                            timeMap.remove(uuidString);

                            return;
                        } else if (message.equals("NO SOLUTION")) {
                            System.err.println(uuidString + " didn't find the solution");
                        }
                    }

                    if ((result == null) && (!tasks.isEmpty())) {
                        Task task = tasks.take();
                        taskMap.put(uuidString, task);
                        timeMap.put(uuidString, System.currentTimeMillis());
                        dataOutputStream.writeUTF("WORK");
                        dataOutputStream.writeLong(task.getLength());
                        dataOutputStream.writeLong(task.getStartForCount());
                        dataOutputStream.writeLong(task.getEndForCount());
                    } else {
                        dataOutputStream.writeUTF("STOPWORK");
                    }

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                }
            }
        }
//    }
}