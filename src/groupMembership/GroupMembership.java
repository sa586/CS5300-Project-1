package groupMembership;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rpc.RPC;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;

public class GroupMembership {
   public static final int checkRate = 10;
   public static final String simpleDBDomain = "Project1";
   AmazonSimpleDB sdb;
   String ip;
   String port;
   Random r = new Random();
   public GroupMembership(String iparg, String portarg) throws IOException {
      ip = iparg;
      port = portarg;
      sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
               GroupMembership.class.getResourceAsStream("AwsCredentials.properties")));
      checkRound();
   }
   
   private boolean checkRound() {
      //Add to SimpleDB
      addMembership();
      
      //Probe random server in SimpleDB
      SelectRequest selectRequest = new SelectRequest("select * from "+simpleDBDomain);
      List<Item> items = sdb.select(selectRequest).getItems();
      int i = r.nextInt(items.size());
      Item item = items.get(i);
      System.out.println("    Name: " + item.getName());
      String remoteip = null;
      String remoteport = null;
      for (Attribute attribute : item.getAttributes()) {
         System.out.println("      Attribute");
         System.out.println("        Name:  " + attribute.getName());
         System.out.println("        Value: " + attribute.getValue());
         if(attribute.getName().equals("ip")) {
            remoteip = attribute.getValue();
         } else if (attribute.getName().equals("port")) {
            remoteport = attribute.getValue();
         } 
      }
      if(remoteip != null && remoteport != null) {
         probe(remoteip,remoteport);
      }

      System.out.println("End");
      return true;
   }
   
   private boolean addMembership() {
      List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();
      replaceableAttributes.add(new ReplaceableAttribute("ip", ip, true));
      replaceableAttributes.add(new ReplaceableAttribute("port", port, true));
      sdb.putAttributes(new PutAttributesRequest(simpleDBDomain,ip+":"+port,replaceableAttributes));
      return true;
   }
   
   /**
    * Issue a probe request to remoteip:remoteport.
    * If the server does not respond, remove it from SimpleDB.
    * @return
    */
   private boolean probe(String remoteip, String remoteport) {
      if (RPC.probe(remoteip, remoteport)) {
         System.out.println(ip+":"+port+" Active");
         return true;
      } else {
         System.out.println(ip+":"+port+" Inactive");
         sdb.deleteAttributes(new DeleteAttributesRequest(simpleDBDomain, remoteip+":"+remoteport));
         return false;
      }
   }
}
