package main.java;

public class FrontControllerServlet extends jakarta.servlet.http.HttpServlet {

     protected void processRequest(jakarta.servlet.http.HttpServletRequest request, 
     jakarta.servlet.http.HttpServletResponse response) 
     throws jakarta.servlet.ServletException, java.io.IOException {
         String url=request.getRequestURI();
         response.getWriter().println("URL: " + url);
        //  request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

     protected void doPost(jakarta.servlet.http.HttpServletRequest request, 
     jakarta.servlet.http.HttpServletResponse response) 
     throws jakarta.servlet.ServletException, java.io.IOException {
         processRequest(request, response);
     }

     protected void doGet(jakarta.servlet.http.HttpServletRequest request,
      jakarta.servlet.http.HttpServletResponse response) 
      throws jakarta.servlet.ServletException, java.io.IOException {
         processRequest(request, response);
     }
} 