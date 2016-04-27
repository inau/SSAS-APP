package dk.itu.ssas.project;

import java.io.*;  
import javax.servlet.*;  
import javax.servlet.http.*;  
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet("/Prompt")
public class Prompt extends HttpServlet {
  private static final long serialVersionUID = 1L;

  public Prompt() {
    super();
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String commands = request.getParameter("cmd_val");
    //this could be set to a specific directory, if desired
    File dir = null;
    BufferedReader is = null;
    BufferedReader es = null;

    try {
        Process process = Runtime.getRuntime().exec(commands);
        String line;
        is = new BufferedReader(new InputStreamReader(process.getInputStream()));
        es = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	String s = org.apache.commons.io.IOUtils.toString(is);
        while((line = es.readLine()) != null) {
            System.err.println(s);
        }

        int exitCode = process.waitFor();
        if (exitCode == 0)
            System.out.println("It worked");
        else
            System.out.println("Something bad happened. Exit code: " + exitCode);

        request.setAttribute("response", s);
	RequestDispatcher rd = request.getRequestDispatcher("/admin.jsp");
	rd.forward(request, response);
    }
    catch(Exception e) {
        System.out.println("Something when wrong: " + e.getMessage());
        e.printStackTrace();
    }
    finally {
        if (is != null) {
            try { is.close(); } 
            catch (IOException e) {}
        }
        if (es != null) {
            try { es.close(); } 
            catch (IOException e) {}
        }
    } //finally
  }
}
