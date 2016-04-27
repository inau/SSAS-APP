package dk.itu.ssas.project;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import dk.itu.ssas.project.DB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Comment
 */
@WebServlet("/Comment")
public class Comment extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Comment() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 try
		 {
			 DB.DBWrapper dbw = DB.getDBWrapper();
			 String img_id = request.getParameter("image_id");
			 String user = request.getParameter("user_id");
			 String comment = request.getParameter("comment");
			 dbw.createCommentForUser(img_id, user, comment);
			 response.sendRedirect("main.jsp");
		 }
		 catch (Exception e) 
		 {
			 throw new ServletException("SQL malfunction.", e);
		 }
	}

}
