
import groupMembership.GroupMembership;
import groupMembership.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rpc.RPCServer;
import session.SessionManager;

/**
 * Servlet implementation class Startup
 */
public class Startup extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public Startup() {
    super();
    // TODO Auto-generated constructor stub
    // Startup server
    Project1.rpcServer = new RPCServer();
    new Thread(Project1.rpcServer).start();

    try {
      // Get IP and Port of RPCServer
      Project1.localServer = new Server(InetAddress.getLocalHost(), Project1.rpcServer.getPort());

      // Start GroupMembership service
      Project1.gm = new GroupMembership(Project1.localServer);
      new Thread(Project1.gm).start();
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

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<h3><a href=\"Project1\">Probably meant to go here</a></h3>");
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request,response);
  }

}
