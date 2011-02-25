package session;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.*;

import com.google.gson.*;


public class SessionManager {
   protected static final String cookieName = "CS5300SESSION";
   protected static final Integer sessionTimeout = 60; //Timeout time in seconds of session
   protected static final Integer sessionCleanerFrequency = 2; //Delay in seconds for running cleaner
   
   protected static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
   protected static final Lock readlock = rwl.readLock();
   protected static final Lock writelock = rwl.writeLock();
   protected static final Hashtable<String, Session> sessions = new Hashtable<String, Session>();
   
   protected static final SessionCleaner sessionCleaner = new SessionCleaner();
   protected static final Timer sessionCleanerTimer = new Timer();
   
   //Retrieve a session from the table or create one
   //Increment the version of the session
   //Save this back as a cookie
   public static Session getAndIncrement(HttpServletRequest request, HttpServletResponse response) {
      Cookie[] cookies = request.getCookies();
      Cookie cookie = null;
      if(cookies != null) {
         for(int i = 0; i < cookies.length; i++) {
            Cookie c = cookies[i];
            if(c.getName().equals(cookieName)) {
               cookie = c;
            }
         }
      }
      if(cookie == null) {
         return initialize(response);
      } else {
         Session session = getSessionFromCookie(cookie.getValue());
         //If we are unable to get session
         if(session == null){
            return initialize(response);
         } else {
            session.incrementVersion();
            cookie.setValue(makeCookie(session));
            response.addCookie(cookie);
            return session;
         }
      }
   }
   
   //Destroy a session
   //Expire the cookie and delete from table
   public static void destroy(HttpServletRequest request, HttpServletResponse response, Session session) {
      Cookie[] cookies = request.getCookies();
      Cookie cookie = null;
      if(cookies != null) {
         for(int i = 0; i < cookies.length; i++) {
            Cookie c = cookies[i];
            if(c.getName().equals(cookieName)) {
               cookie = c;
            }
         }
      }
      if(cookie != null) {
         //Expire the cookie
         cookie.setMaxAge(0);
         response.addCookie(cookie);
      }
      writelock.lock();
      try {
         sessions.remove(session.getSessionID());
      } finally {
         writelock.unlock();
      }
   }
   //Start the Session table cleaner
   public static void startCleaner() {
      sessionCleanerTimer.schedule(sessionCleaner, sessionCleanerFrequency*1000, sessionCleanerFrequency*1000);
   }
   //Clean up loose threads
   public static void cleanup() {
      sessionCleanerTimer.cancel();
   }
   
   //Create a new session and add cookie for session
   private static Session initialize(HttpServletResponse response) {
      String uuid = UUID.randomUUID().toString();
      Session s = new Session(uuid, new ArrayList<Integer>(1));
      writelock.lock();
      try {
         sessions.put(uuid, s);
      } finally {
         writelock.unlock();
      }
      Cookie c = new Cookie(cookieName,makeCookie(s));
      c.setMaxAge(sessionTimeout);
      response.addCookie(c);
      return s;
   }
   //Create a cookie string
   private static String makeCookie(Session s) {
      Gson gson = new Gson();
      String[] cookie_params = {s.getSessionID(), s.getVersion(), s.getLocations()};
      try {
         return URLEncoder.encode(gson.toJson(cookie_params),"UTF-8");
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return null;
   }
   //Find session from a given cookie string
   //Returns null if unable to be found or version numbers are incorrect
   private static Session getSessionFromCookie(String cookie) {
      Gson gson = new Gson();
      String[] cookie_params;
      try {
         cookie_params = gson.fromJson(URLDecoder.decode(cookie,"UTF-8"), String[].class);
         if((cookie_params == null) || (cookie_params.length < 3)) {
            return null;
         } else {
            readlock.lock();
            try {
               Session session = sessions.get(cookie_params[0]);
               if(session == null) {
                  return null;
               } else {
                  //Check to see if the versions match
                  if(session.getVersion().equals(cookie_params[1])) {
                     return session;
                  } else {
                     return null;
                  }
               }
            } finally {
               readlock.unlock();
            }
         }
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return null;

   }
}
