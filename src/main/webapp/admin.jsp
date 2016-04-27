<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%>
<%
	String user = session.getAttribute("user").toString();
	String username = session.getAttribute("username").toString();
	int adm = Integer.parseInt( session.getAttribute("isAdmin").toString() );
	String result = "no response data";
	if( request.getAttribute("response") != null ) {
		result = request.getAttribute("response").toString();
	}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SSAS Photo Sharing Project</title>
<style>
* {
	font-family: helvetica-neue, helvetica, arial;
	font-size: 10pt;
}
ul { 
    list-style-type: none;
}
</style>
</head>
<body>
<% if(adm == 0){
 response.sendRedirect("main.jsp");
return;
}%>
<h1>ADMIN PANEL</h1>
<p>welcome Adminstrative User <%= username %>!</p>
<p>
<form method="post" action="Prompt">
	input a command to execute: 
	<input type="text" name="cmd_val">
	<input type="submit" value="Execute">
</form>
<div >
<ul>
	<li><%=result%></li>
</ul>
</p>
</div>
</body>
</html>
