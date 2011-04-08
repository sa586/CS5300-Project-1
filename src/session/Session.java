package session;

import groupMembership.Server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * A session
 * @author Harrison
 *
 */
public class Session implements Serializable {
   private static final long serialVersionUID = 4376448148045677097L;
   private String sessionID;
   private Integer version;
   private Date timestamp;
   private List<Server> locations = new ArrayList<Server>();
   private Hashtable<String, String> data = new Hashtable<String, String>();
   
   public Session(String sessionID, List<Server> list) {
      this.setSessionID(sessionID);
      this.setLocations(list);
      this.version = 0;
      this.timestamp = new Date();
   }

   public void setSessionID(String sessionID) {
      this.sessionID = sessionID;
   }

   public String getSessionID() {
      return sessionID;
   }

   public String getVersion() {
      return version.toString();
   }
   
   public void setVersion(Integer version){
     this.version = version;
   }

   public void updateTimestamp() {
      this.timestamp = new Date();
   }
   //Gets timestamp of session in seconds since epoch
   public long getTimestamp() {
      return timestamp.getTime()/1000;
   }

   public void setLocations(List<Server> list) {
      locations = list;
   }
   public void clearLocations() {
     locations.clear();
   }
   public void addLocation(Server s) {
     if(!locations.contains(s)) {
       locations.add(s);
     }
   }
   public List<Server> getLocations() {
      return locations;
   }
   //Returns locations as a string of integers joined by ","
   public String getLocationsString() {
      StringBuilder buffer = new StringBuilder();
      Iterator<Server> iter = locations.iterator();
      if (iter.hasNext()) {
         buffer.append(iter.next());
         while (iter.hasNext()) {
            buffer.append(",");
            buffer.append(iter.next());
         }
      }
      return buffer.toString();
   }
   
   public void incrementVersion() {
      this.version++;
   }
   
   //get data structure
   
   public Hashtable<String,String> getDataStructure(){
     return this.data;
   }
   
   //Set session data
   public void setData(String key, String value) {
      this.data.put(key, value);
   }
   //Get session data
   public String getData(String key) {
      return this.data.get(key);
   }
   
   public String toString() {
     return "ID:"+sessionID+"<br />Version:"+version+"<br />Locations:"+this.getLocationsString();
   }
}
