package groupMembership;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
   public InetAddress ip;
   public Integer port;
   
   /**
    * Dummy constructor
    */
   public Server() {
      try {
         ip = InetAddress.getByName("127.0.0.1");
      } catch (UnknownHostException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      port = 0;
   }
   
   public Server(String sip, String sport) {
      try {
         ip = InetAddress.getByName(sip);
      } catch (UnknownHostException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      port = new Integer(sport);
   }
   
   public Server(String ipAndPort) {
      String[] parts = ipAndPort.split(":");
      try {
         ip = InetAddress.getByName(parts[0]);
         port = new Integer(parts[1]);
      } catch (UnknownHostException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public String toString() {
      return ip.getHostAddress()+":"+port;
   }
   
   public boolean equals(Server s2) {
      return (ip == s2.ip && port == s2.port);
   }
}
