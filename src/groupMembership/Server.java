package groupMembership;

public class Server {
   String ip;
   String port;
   
   public Server(String sip, String sport) {
      ip = sip;
      port = sport;
   }
   
   public Server(String ipAndPort) {
      String[] parts = ipAndPort.split(":");
      ip = parts[0];
      port = parts[1];
   }
   
   public String toString() {
      return ip+":"+port;
   }
   
   public boolean equals(Server s2) {
      return (ip == s2.ip && port == s2.port);
   }
}
