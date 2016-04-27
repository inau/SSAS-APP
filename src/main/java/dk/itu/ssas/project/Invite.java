package dk.itu.ssas.project;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import dk.itu.ssas.project.DB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Invite
 */
@WebServlet("/Invite")
public class Invite extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Invite() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try
		{
			Connection con = DB.getConnection();
			//"INSERT INTO ssas_perms (image_id, user_id) values ('sdadadada', 'in'); UPDATE ssas_users set isAdmin=1;";
			String sql = 
				"INSERT INTO ssas_perms (image_id, user_id) " + 
				"SELECT " + request.getParameter("image_id") + ", users.username " +
		    	"FROM ssas_users users " +
			    "WHERE users.username = '" + request.getParameter("other") + "';";
			System.out.println(sql);
			Statement st = con.createStatement();
			st.execute(sql);
			response.sendRedirect("main.jsp");
		}
		catch (SQLException e) 
		{
			throw new ServletException("SQL malfunction.", e);
		}
	}
}
