package rpc;

public class RPC {
   
   static {
      
   }
   /**
    * Return true if probe succeeded, false otherwise
    * @return
    */
   public static boolean probe(String ip, String port) {
      System.out.println("Probing "+ip+":"+port);
      return true;
   }
}
