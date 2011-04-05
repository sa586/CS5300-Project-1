package session;

import groupMembership.Server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * A session
 * @author Harrison
 *
 */
public class Session implements Serializable {
   /**
    * 
    */
   private static final long serialVersionUID = 4376448148045677097L;
   private String sessionID;
   private Integer version;
   private Date timestamp;
   private ArrayList<Server> locations;
   private Hashtable<String, Object> data;
   
   public Session(String sessionID, ArrayList<Server> location) {
      this.setSessionID(sessionID);
      this.setLocations(location);
      this.version = 0;
      this.timestamp = new Date();
      this.data = new Hashtable<String, Object>();
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

   public void updateTimestamp() {
      this.timestamp = new Date();
   }
   //Gets timestamp of session in seconds since epoch
   public long getTimestamp() {
      return timestamp.getTime()/1000;
   }

   public void setLocations(ArrayList<Server> locations) {
      this.locations = locations;
   }
   public ArrayList<Server> getLocations() {
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
   //Set session data
   public void setData(String key, Object value) {
      this.data.put(key, value);
   }
   //Get session data
   public Object getData(String key) {
      return this.data.get(key);
   }
   
}
