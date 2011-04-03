import java.net.InetAddress;

import rpc.RPCServer;

import groupMembership.GroupMembership;
import groupMembership.Server;


public class Project1 {

   /**
    * @param args
    * @throws Exception 
    */
   public static void main(String[] args) throws Exception {
      //Startup server
      RPCServer server = new RPCServer();
      new Thread(server).start();
      
      String serverIP = InetAddress.getLocalHost().getHostAddress();
      String serverPort = Integer.toString(server.getPort());
      
      GroupMembership gm = new GroupMembership(new Server(serverIP, serverPort));
      new Thread(gm).start();

   }

}
