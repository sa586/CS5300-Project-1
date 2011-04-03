package rpc;

import groupMembership.Server;

import java.util.List;

import session.Session;

public class RPCClient {
   
   static {
      
   }
   /**
    * Return true if probe succeeded, false otherwise
    * @return
    */
   public static boolean probe(String ip, String port) {
      System.out.println("Probing "+ip+":"+port);
      return false;
   }
   
   /**
    * Find session data from SSM servers, return null if not found
    * @return
    */
   public static Session get(Session s) {
      return null;
   }
   
   /**
    * Send call to several destinations, and return the first NQ responses
    * @return
    */
   public static List<Server> put(Session s) {
      return null;
   }
   
}
