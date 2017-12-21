import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    public PortForwarder(int lPort, int rPort, String rHost) {
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

    public void startWork() {
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
                        } else if (key.isReadable()) {
                            // Читаем данные
                            read(key);
                        } else if (key.isWritable()) {
                            // Пишем данные
                            write(key);

                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        close(key);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());

        }
    }

    private void accept() throws IOException {
            SocketChannel firstChannel = serverSocketChannel.accept();
            firstChannel.configureBlocking(false);
            System.out.println("New connection. local address " + firstChannel.getLocalAddress() + " ,remote address" + firstChannel.getRemoteAddress());

            SocketChannel secondChannel = SocketChannel.open();
            secondChannel.configureBlocking(false);
            boolean isConnected = secondChannel.connect(inetSocketAddress);

            if (!isConnected) {
                secondChannel.register(selector, SelectionKey.OP_CONNECT);
            }

            connectionMap.put(firstChannel, secondChannel);
            connectionMap.put(secondChannel, firstChannel);

            byteBuffer.put(firstChannel, ByteBuffer.allocate(BUF_SIZE));
            byteBuffer.put(secondChannel, ByteBuffer.allocate(BUF_SIZE));
    }

    private void read(SelectionKey selectionKey) throws IOException {
        try {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            System.out.println("New input. local address " + channel.getLocalAddress() + " remote address " + channel.getRemoteAddress());
            ByteBuffer buffer = byteBuffer.get(channel);
            int read = channel.read(buffer);
            SocketChannel anotherChannel = connectionMap.get(channel);
            buffer.flip();
            anotherChannel.write(buffer);

            buffer.flip();
            System.out.println("Wrote data from input");
            if (read == -1) {
                channel.shutdownOutput();//Shutdown the connection for writing without closing the channel.
                System.out.println("Shutdown input");

            }
        } catch (ClosedChannelException e) {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            SocketChannel anotherChannel = (SocketChannel) selectionKey.channel();
            channel.close();
            anotherChannel.close();
            connectionMap.remove(channel);
            connectionMap.remove(anotherChannel);
            byteBuffer.remove(channel);
            byteBuffer.remove(anotherChannel);
        }
    }

    private void write(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        System.out.println("New output. local address " + channel.getLocalAddress() + " remote address " + channel.getRemoteAddress());
        SocketChannel anotherSocketChannel = connectionMap.get(channel);
        ByteBuffer buffer = byteBuffer.get(anotherSocketChannel);
        buffer.flip();
        channel.write(buffer);

        buffer.flip();
        System.out.println("Wrote data from output");
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.finishConnect();
        // Завершаем соединение

        SocketChannel clientSocketChannel = connectionMap.get(channel);

        clientSocketChannel.register(selector, SelectionKey.OP_READ);
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void close(SelectionKey key) throws IOException {
        key.cancel();
        key.channel().close();
        key.interestOps(SelectionKey.OP_WRITE);

    }
}