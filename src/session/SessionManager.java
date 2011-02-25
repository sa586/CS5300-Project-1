package session;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Date;
import javax.servlet.http.*;


public class SessionManager {
   private static Hashtable<String, Session> sessions = new Hashtable<String, Session>();
   public void initialize() {
      String uuid = UUID.randomUUID().toString();
      Session s = new Session(uuid, 0, new Date(), new ArrayList<Integer>(1));
      sessions.put(uuid, s);
      new Cookie("session_id", uuid);
      new Cookie("version", s.getVersion());
      new Cookie("locations", s.getLocations());
   }
}
