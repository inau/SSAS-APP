<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import = "java.sql.*"
    import = "dk.itu.ssas.project.DB" 
%>
<% 
	String user = request.getParameter("username");   
    String pwd = request.getParameter("password");
	
    DB.getDBWrapper().createNewUser(user, pwd);
		
	ResultSet rs = DB.getDBWrapper().FindUserByName(user);
	
	rs.next();
	session.setAttribute("user", rs.getString(1));
	session.setAttribute("username", user);
	session.setAttribute("isAdmin", rs.getString(4) );
	response.sendRedirect("main.jsp");
%>
