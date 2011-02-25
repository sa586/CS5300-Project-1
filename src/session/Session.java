package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Session {
   private String sessionID;
   private Integer version;
   private Date timestamp;
   private ArrayList<Integer> locations;
   
   public Session(String sessionID, Integer version, Date timestamp, ArrayList<Integer> location) {
      this.setSessionID(sessionID);
      this.setVersion(version);
      this.setTimestamp(timestamp);
      this.setLocations(location);
   }

   public void setSessionID(String sessionID) {
      this.sessionID = sessionID;
   }

   public String getSessionID() {
      return sessionID;
   }

   public void setVersion(Integer version) {
      this.version = version;
   }

   public String getVersion() {
      return version.toString();
   }

   public void setTimestamp(Date timestamp) {
      this.timestamp = timestamp;
   }

   public String getTimestamp() {
      return timestamp.toString();
   }

   public void setLocations(ArrayList<Integer> locations) {
      this.locations = locations;
   }

   public String getLocations() {
      StringBuilder buffer = new StringBuilder();
      Iterator<Integer> iter = locations.iterator();
      if (iter.hasNext()) {
         buffer.append(iter.next());
         while (iter.hasNext()) {
            buffer.append(",");
            buffer.append(iter.next());
         }
      }
      return buffer.toString();
   }
}
