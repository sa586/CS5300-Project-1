package session;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimerTask;

   
public class SessionCleaner extends TimerTask {
   public void run() {
      SessionManager.writelock.lock();
      long now = (new Date().getTime()) / 1000;
      try {
         Hashtable<String,Session> h = SessionManager.sessions;
         for(Enumeration<String> e = h.keys(); e.hasMoreElements();) {
            String key = e.nextElement();
            Session s = h.get(key);
            if((now - s.getTimestamp()) > SessionManager.sessionTimeout) {
               h.remove(key);
               System.out.println("Removed "+key);
            }
         }
         System.out.println("Cleaner Run");
      } finally {
         SessionManager.writelock.unlock();
      }
      
   }
}
