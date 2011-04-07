import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rpc.RPCServer;
import session.Session;
import session.SessionManager;

import groupMembership.GroupMembership;
import groupMembership.Server;

public class Project1 extends HttpServlet {
  private static final long serialVersionUID = 8815322823956211829L;

  private Server localServer;

  private RPCServer rpcServer;
  private GroupMembership gm;

  /**
   * @param args
   * @throws Exception
   */
  public Project1() {
    super();
    // Startup server
    rpcServer = new RPCServer();
    new Thread(rpcServer).start();


    try {
      // Get IP and Port of RPCServer
      localServer = new Server(InetAddress.getLocalHost(), rpcServer.getPort());

      // Start GroupMembership service
      gm = new GroupMembership(localServer);
      new Thread(gm).start();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // Start session cleaner
    SessionManager.startCleaner();
  }

  /*
  public void destroy() {
    rpcServer.cleanup();
    gm.cleanup();
    SessionManager.cleanup();
    
    rpcServer.destroy();
    gm.destroy();
  }
  */

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Session session = SessionManager.getAndIncrement(request, response);
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    String message = (String) session.getData("message");
    String count = session.getData("count");
    // Initialize message
    if (message == null) {
      message = "Hello World!";
    }
    if (count == null) {
      count = "1";
    } else {
      count = (new Integer(1 + Integer.parseInt(count))).toString();
    }
    String cmd = request.getParameter("cmd");
    if (cmd != null) {
      if (cmd.equals("Replace")) {
        message = request.getParameter("replace_text");
        count = "1";
      } else if (cmd.equals("LogOut")) {
        SessionManager.destroy(request, response, session);
        out.println("<!DOCTYPE html>");
        out.println("<html><head></head><body>");
        out.println("<h2>Bye!</h2>");
        out.println("</body></html>");
        return;
      }
    }
    session.setData("message", message);
    session.setData("count", new Integer(count).toString());
    out.println("<!DOCTYPE html>");
    out.println("<html><head></head><body>");
    out.println("<h2>(" + count.toString() + ") " + message + "</h2>");
    out.println("<form method=\"post\">");
    out.println("<div><input type=\"submit\" value=\"Replace\" name=\"cmd\" /><input type=\"text\" name=\"replace_text\" /></div>");
    out.println("<div><input type=\"submit\" value=\"Refresh\" name=\"cmd\" /></div>");
    out.println("<div><input type=\"submit\" value=\"LogOut\" name=\"cmd\" /></div>");
    out.println("</form>");
    out.println("<h3>Server: "+localServer+"</h3>");
    out.println("<h3>SessionID: "+session.getSessionID()+"</h3>");
    out.println("<h3>Version: "+session.getVersion()+"</h3>");
    out.println("<h3>Session: "+session+"</h3>");
    out.println("</body></html>");
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response) Simply refer to doGet
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }

}
