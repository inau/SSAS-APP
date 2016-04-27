<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
    import = "java.sql.*"
    import = "dk.itu.ssas.project.DB"
%>
<%
    String user = request.getParameter("username");   
    String pwd = request.getParameter("password");
   
    DB.DBWrapper dbw = DB.getDBWrapper();
   
	ResultSet rs = dbw.login(user, pwd);
	
    if (rs.next()) {
    	// Have a result; user is authenticated.
		System.out.println("credentials matched to user");
    	session.setAttribute("user", rs.getString(1));
    	session.setAttribute("username", user);
	session.setAttribute("isAdmin", rs.getInt(4));
    	response.sendRedirect("main.jsp");
    } else {
    	// No result; user failed to authenticate; try again.
session.invalidate();
    	response.sendRedirect("index.jsp?login_failure=1");
    }
%>
