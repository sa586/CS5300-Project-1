import java.io.IOException;
import java.net.InetAddress;

import groupMembership.GroupMembership;


public class Project1 {

   /**
    * @param args
    * @throws Exception 
    */
   public static void main(String[] args) throws Exception {
      String thisIp = InetAddress.getLocalHost().getHostAddress();
      GroupMembership gm = new GroupMembership(thisIp,"2");

   }

}
