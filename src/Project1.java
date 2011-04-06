import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServlet;

import rpc.RPCServer;

import groupMembership.GroupMembership;
import groupMembership.Server;


public class Project1 extends HttpServlet {
  private static final long serialVersionUID = 8815322823956211829L;
  private RPCServer rpcServer;
  private Server localServer;
  private GroupMembership gm;

  /**
    * @param args
    * @throws Exception 
    */
   public Project1() {
      //Startup server
      rpcServer = new RPCServer();
      new Thread(rpcServer).start();
      
      //Get IP and Port of RPCServer
      try {
        localServer = new Server(InetAddress.getLocalHost(),rpcServer.getPort());
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      //Start GroupMembership service
      
      try {
        gm = new GroupMembership(localServer);
        new Thread(gm).start();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
   }
   public void destroy() {
     rpcServer.cleanup();
     gm.cleanup();
   }

}
