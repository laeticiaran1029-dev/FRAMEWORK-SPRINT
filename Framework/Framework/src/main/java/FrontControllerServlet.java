package main.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import framework.utils.ClassScanner;
import framework.utils.Mapping; 
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;

public class FrontControllerServlet extends HttpServlet {
    private HashMap<String, Mapping> urlMappings = new HashMap<>();
    private List<String> controllersList = new ArrayList<>();
    @Override
    public void init() throws ServletException {
        String packageToScan = getInitParameter("controller-package");
        
        if (packageToScan != null) {
            try {
               this.controllersList = ClassScanner.findControllers(packageToScan);
                for (String className : controllersList) {
                    Class<?> clazz = Class.forName(className);
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(annotation.Mapping.class)) {
                            annotation.Mapping mappingAnnotation = method.getAnnotation(annotation.Mapping.class);
                            String url = mappingAnnotation.url();
                            urlMappings.put(url, new Mapping(className, method.getName()));
                        }
                    }
                }
                System.out.println("Scan terminé. URLs trouvées et mappées : " + urlMappings.keySet());
            } catch (Exception e) {
                throw new ServletException("Erreur lors du scan du package", e);
            }
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = requestURI.substring(contextPath.length());

        if (urlMappings.containsKey(url)) {
            try {
                // 1. Récupérer les infos
                Mapping mapping = urlMappings.get(url);
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method method = clazz.getDeclaredMethod(mapping.getMethodName());
                method.invoke(instance);
                
                out.println("<h1>Succès !</h1>");
                out.println("<p>La méthode <b>" + mapping.getMethodName() + "</b> a bien été exécutée par le Framework !</p>");
              out.println("<h2>Liste des contrôleurs trouvés :</h2>");
                out.println("<ul>");
                for (String nomController : this.controllersList) {
                    out.println("<li>" + nomController + "</li>");
                }
                out.println("</ul>");
                
            } catch (Exception e) {
                out.println("Erreur d'exécution : " + e.getMessage());
            }
        } else {
            out.println("<h1>Erreur 404</h1>");
            out.println("<p>Aucune méthode n'est associée à l'URL : " + url + "</p>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}