

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import session.*;

/**
 * Servlet implementation class Assign3
 */
public class Assign3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   /**
    * @see HttpServlet#HttpServlet()
    */
   public Assign3() {
       super();
       SessionManager.startCleaner();
   }
   /**
    * Called when server is shutdown/restarted/reloaded
    * Ensure that loose threads are removed
    */
   public void destroy() {
      SessionManager.cleanup();
   }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   Session session = SessionManager.getAndIncrement(request, response);
	   response.setContentType("text/html");
	   PrintWriter out = response.getWriter();
	   String message = (String)session.getData("message");
	   Integer count = (Integer)session.getData("count");
	   //Initialize message
	   if(message == null) {
	      message = "Hello World!";
	   }
	   //Initialize count or increment
	   if(count == null) {
	      count = 1;
	   } else {
	      count++;
	   }
	   String cmd = request.getParameter("cmd");
	   if(cmd != null) {
	      if(cmd.equals("Replace")) {
   	      message = request.getParameter("replace_text");
   	      count = 1;
	      } else if(cmd.equals("LogOut")) {
	         SessionManager.destroy(request, response, session);
	         out.println("<!DOCTYPE html>");
	         out.println("<html><head></head><body>");
	         out.println("<h2>Bye!</h2>");
	         out.println("</body></html>");
	         return;
	      }
	   }
	   session.setData("message",message);
	   session.setData("count",count);
	   out.println("<!DOCTYPE html>");
	   out.println("<html><head></head><body>");
	   out.println("<h2>("+count.toString()+") "+message+"</h2>");
	   out.println("<form method=\"post\">");
	   out.println("<div><input type=\"submit\" value=\"Replace\" name=\"cmd\" /><input type=\"text\" name=\"replace_text\" /></div>");
	   out.println("<div><input type=\"submit\" value=\"Refresh\" name=\"cmd\" /></div>");
	   out.println("<div><input type=\"submit\" value=\"LogOut\" name=\"cmd\" /></div>");
	   out.println("</form>");
	   //out.println("<h3>SessionID: "+session.getSessionID()+"</h3>");
	   //out.println("<h3>Version: "+session.getVersion()+"</h3>");
	   out.println("</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * Simply refer to doGet
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   doGet(request,response);
	}

}
