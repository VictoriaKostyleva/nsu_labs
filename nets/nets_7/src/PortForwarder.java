import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PortForwarder {
    private static final int BUF_SIZE = 8192;
    private int lPort;
    private int rPort;
    private String rHost;
    private InetSocketAddress inetSocketAddress;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private Map<SocketChannel, SocketChannel> connectionMap = new HashMap<>();
    private Map<SocketChannel, ByteBuffer> byteBuffer = new HashMap<>();

    private PortForwarder(int lPort, int rPort, String rHost) {
        this.lPort = lPort;
        this.rPort = rPort;
        this.rHost = rHost;
    }

    public static void main(String[] args) {
        int lPort = Integer.parseInt(args[0]);
        String rHost = args[1];
        int rPort = Integer.parseInt(args[2]);

        PortForwarder portForwarder = new PortForwarder(lPort, rPort, rHost);
        portForwarder.startWork();
    }

    private void startWork() {
        try {
            InetAddress inetAddress = InetAddress.getByName(rHost);
            inetSocketAddress = new InetSocketAddress(inetAddress, rPort);

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(lPort));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Listening on port " + lPort);

            while (selector.select() > -1) {

                Iterator iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            // Принимаем соединение
                            accept();
                        } else if (key.isConnectable()) {
                            // Устанавливаем соединение
                            connect(key);
                        } else {
                            if (key.isReadable()) {
                                // Читаем данные
                                read(key);
                            }
                            if (key.isWritable()) {
                                // Пишем данные
                                write(key);

                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());

        }
    }

    private void accept() throws IOException {
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);

            SocketChannel secondChannel = SocketChannel.open();
            secondChannel.configureBlocking(false);
            boolean isConnected = secondChannel.connect(inetSocketAddress);

            if (!isConnected) {
                secondChannel.register(selector, SelectionKey.OP_CONNECT);
            } else {
                channel.register(selector, SelectionKey.OP_READ);
                secondChannel.register(selector, SelectionKey.OP_READ);
            }

            connectionMap.put(channel, secondChannel);
            connectionMap.put(secondChannel, channel);

            byteBuffer.put(channel, ByteBuffer.allocate(BUF_SIZE));
            byteBuffer.put(secondChannel, ByteBuffer.allocate(BUF_SIZE));
    }

    private void read(SelectionKey selectionKey) throws IOException {
        try {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            SocketChannel anotherChannel = connectionMap.get(channel);
            Set<SelectionKey> keys = selector.keys();//

            ByteBuffer buffer = byteBuffer.get(channel);
            int read = channel.read(buffer);

            System.out.println("Read " + read + " bytes");

            for (SelectionKey key : keys) {
                if (key.channel().equals(anotherChannel)) {
                    resumeOp(key, SelectionKey.OP_WRITE);
                }
            }
            stopOp(selectionKey, SelectionKey.OP_READ);

            System.out.println("Wrote data from input");
            if (read == -1) {

                if(buffer.position() != 0) {

                    for (SelectionKey key : keys) {
                        if (key.channel().equals(anotherChannel)) {
                            resumeOp(key, SelectionKey.OP_WRITE);
                        }
                    }
                }
                else {
                    anotherChannel.shutdownOutput();
                }

                if(byteBuffer.get(channel).position() == 0 && byteBuffer.get(anotherChannel).position() == 0) {


                    closeChannel(channel);
                    closeChannel(anotherChannel);
                    connectionMap.remove(channel);
                    connectionMap.remove(anotherChannel);
                    byteBuffer.remove(channel);
                    byteBuffer.remove(anotherChannel);
                }
            }

        } catch (IOException e) {//close не только тут надо. делать тогда, когда уверены, что ни в какую сторону данных не будет.
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            SocketChannel anotherChannel = (SocketChannel) selectionKey.channel();
            closeChannel(channel);
            closeChannel(anotherChannel);
            connectionMap.remove(channel);
            connectionMap.remove(anotherChannel);
            byteBuffer.remove(channel);
            byteBuffer.remove(anotherChannel);
        }
    }

    private void write(SelectionKey selectionKey) throws IOException {
        try {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        SocketChannel anotherChannel = connectionMap.get(channel);

        ByteBuffer buffer = byteBuffer.get(anotherChannel);
        buffer.flip();

        int writeBytes = channel.write(buffer);
        System.out.println("Write " + writeBytes + " bytes");

        buffer.compact();

            if(buffer.position() == 0) {
                System.out.println("Buffer is full (Output)");
                stopOp(selectionKey, SelectionKey.OP_WRITE);

                Set<SelectionKey> keys = selector.keys();
                for (SelectionKey key : keys) {
                    if (key.channel().equals(anotherChannel)) {
                        resumeOp(key, SelectionKey.OP_READ);
                    }
                }
            }

        System.out.println("Wrote data from output");

        } catch (IOException e) {
            System.out.println("Another channel closed");
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            SocketChannel anotherChannel = (SocketChannel) selectionKey.channel();
            closeChannel(channel);
            closeChannel(anotherChannel);
            connectionMap.remove(channel);
            connectionMap.remove(anotherChannel);
            byteBuffer.remove(channel);
            byteBuffer.remove(anotherChannel);
        }
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        boolean isConnected = channel.finishConnect();
        SocketChannel anotherChannel = connectionMap.get(channel);
        // Завершаем соединение
        if(!isConnected) {
            System.out.println("Haven't connected yet");
            connectionMap.remove(channel);
            connectionMap.remove(anotherChannel);
            byteBuffer.remove(channel);
            byteBuffer.remove(anotherChannel);
            return;
        }

        System.out.println("Connect response");

        anotherChannel.register(selector, SelectionKey.OP_READ);
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void closeChannel(SocketChannel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void stopOp(SelectionKey selectionKey, int option) {
        selectionKey.interestOps(selectionKey.interestOps() & ~option);
    }

    private void resumeOp(SelectionKey selectionKey, int option) {
        selectionKey.interestOps(selectionKey.interestOps() | option);
    }

}