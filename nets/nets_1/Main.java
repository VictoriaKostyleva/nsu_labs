import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main {
  private static final int TIMEOUT = 3000;
  private static final int TIMEKILL = 10000;

  public static void main(String[] args) throws IOException{

    boolean flag = false;

    Map<InetAddress, Long> map = new HashMap<InetAddress, Long>();
    InetAddress group = InetAddress.getByName("228.5.6.7");
    MulticastSocket s = new MulticastSocket(6789);
    s.joinGroup(group);
    s.setSoTimeout(TIMEOUT);

    DatagramPacket sPackage = new DatagramPacket(new byte[0], 0, group, 6789);
    DatagramPacket rPackage = new DatagramPacket(new byte[0], 0);

    long lastSendTime = System.currentTimeMillis();
    Iterator<Map.Entry<InetAddress, Long>> iterator;

    for(;;){
      if (TIMEOUT < System.currentTimeMillis() - lastSendTime){
          lastSendTime = System.currentTimeMillis();
          System.out.println("Send");
          s.send(sPackage);
      }
      try{
        s.receive(rPackage);
        if(map.get(rPackage.getAddress()) == null){
          flag = true;
        }
        map.put(rPackage.getAddress(), System.currentTimeMillis());
      } catch (SocketTimeoutException ex) {

      }

      iterator = map.entrySet().iterator();

      while (iterator.hasNext()){
          Map.Entry<InetAddress, Long> tmp = iterator.next();

          if (TIMEKILL < (System.currentTimeMillis() - tmp.getValue())){
              iterator.remove();
              flag = true;
          }
      }
      if (flag == true){
        printMap(map);
        flag = false;
      }
    }
  }

  private static void printMap(Map<InetAddress,Long> map){
    Iterator<Map.Entry<InetAddress, Long>> iterator = map.entrySet().iterator();;
    System.out.println("IP List:");
    while (iterator.hasNext()){
        Map.Entry<InetAddress, Long> tmp = iterator.next();

        System.out.println(tmp.getKey());
      }
    System.out.println("\n");

  }
}
