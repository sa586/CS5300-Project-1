import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rpc.RPCClient;
import rpc.RPCServer;
import session.Session;
import session.SessionManager;

import groupMembership.GroupMembership;
import groupMembership.Server;

public class Project1 extends HttpServlet {
  private static final long serialVersionUID = 8815322823956211829L;

  public static Server localServer;

  public static RPCServer rpcServer;
  public static GroupMembership gm;


  /**
   * @param args
   * @throws Exception
   */
  public Project1() {
    super();
  }

  /*
   * public void destroy() { rpcServer.cleanup(); gm.cleanup();
   * SessionManager.cleanup();
   * 
   * rpcServer.destroy(); gm.destroy(); }
   */

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Set response objects
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    // Load session from cookie or create new one if doesn't exist
    Session session = SessionManager.getAndIncrement(request);

    String message = (String) session.getData("message");
    String count = session.getData("count");
    // Initialize message
    if (message == null) {
      message = "Hello World!";
    }
    // Initialize count or increment
    if (count == null) {
      count = "1";
    } else {
      count = (new Integer(1 + Integer.parseInt(count))).toString();
    }
    // Check user submission command
    String cmd = request.getParameter("cmd");
    if (cmd != null) {
      if (cmd.equals("Replace")) {
        message = request.getParameter("replace_text");
        count = "1";
      } else if (cmd.equals("LogOut")) {
        // Do something different on logout
        SessionManager.destroy(request, response, session);
        out.println("<!DOCTYPE html>");
        out.println("<html><head></head><body>");
        out.println("<h2>Bye!</h2>");
        out.println("</body></html>");
        return;
      }
    }
    // Write changed variables back to session
    session.setData("message", message);
    session.setData("count", count);

    // Attempt put
    if (RPCClient.put(session) == null) {
      // If put fails, try putting to as many as possible
      session.setLocations(GroupMembership.getServers());
      RPCClient.put(session);
    }
    // Write back cookie
    SessionManager.putCookie(response, session);

    // Output HTML to page
    out.println("<!DOCTYPE html>");
    out.println("<html><head></head><body>");
    out.println("<h2>(" + count.toString() + ") " + message + "</h2>");
    out.println("<form method=\"post\">");
    out.println("<div><input type=\"submit\" value=\"Replace\" name=\"cmd\" /><input type=\"text\" name=\"replace_text\" /></div>");
    out.println("<div><input type=\"submit\" value=\"Refresh\" name=\"cmd\" /></div>");
    out.println("<div><input type=\"submit\" value=\"LogOut\" name=\"cmd\" /></div>");
    out.println("</form>");
    out.println("<h3>Server: " + localServer + "</h3>");
    out.println("<h3>Session: " + session + "</h3>");
    out.println("</body></html>");
  }

  /**
   * Simply refer to doGet
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }

}
