package main.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public class FrontControllerServlet extends HttpServlet {
    private final HashMap<framework.utils.RouteKey, framework.utils.Mapping> urlMappings = new HashMap<>();
    private List<String> controllersList = new ArrayList<>();
    
    @Override
    public void init() throws ServletException {
        String packageToScan = getInitParameter("controller-package");
        
        if (packageToScan != null) {
            try {
                this.controllersList = framework.utils.ClassScanner.findControllers(packageToScan);
                for (String className : controllersList) {
                    Class<?> clazz = Class.forName(className);
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(annotation.Mapping.class)) {
                            annotation.Mapping mappingAnnotation = method.getAnnotation(annotation.Mapping.class);
                            framework.utils.RouteKey routeKey = new framework.utils.RouteKey(mappingAnnotation.url(), mappingAnnotation.method());
                            if (urlMappings.containsKey(routeKey)) {
                                throw new ServletException("Route déjà déclarée : " + routeKey);
                            }
                            urlMappings.put(routeKey, new framework.utils.Mapping(className, method.getName()));
                        }
                    }
                }
                System.out.println("Scan terminé. Routes trouvées et mappées : " + urlMappings.keySet());
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
        framework.utils.RouteKey routeKey = new framework.utils.RouteKey(url, request.getMethod());

        if (urlMappings.containsKey(routeKey)) {
            try {
            framework.utils.Mapping mapping = urlMappings.get(routeKey);
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method method = clazz.getDeclaredMethod(mapping.getMethodName());
                method.invoke(instance);
                out.println("<h1>Succès : Affichage des résultats</h1>");
                out.println("<p>Classe : <b>" + mapping.getClassName() + "</b></p>");
                out.println("<p>&nbsp;&nbsp; - URL : <b>" + url + "</b></p>");
                out.println("<p>&nbsp;&nbsp; - Méthode HTTP : <b>" + routeKey.getMethod() + "</b></p>");
                out.println("<p>&nbsp;&nbsp; - Méthode : <b>" + mapping.getMethodName() + "</b></p>");
                
            } catch (Exception e) {
                out.println("Erreur d'exécution : " + e.getMessage());
            }
        } else {
            StringBuilder routesSupportees = new StringBuilder();
            routesSupportees.append("\n");
            
            for (java.util.Map.Entry<framework.utils.RouteKey, framework.utils.Mapping> entry : urlMappings.entrySet()) {
                framework.utils.RouteKey routeValide = entry.getKey();
                framework.utils.Mapping mapping = entry.getValue();
                
                routesSupportees.append("Classe : ").append(mapping.getClassName()).append("\n")
                                .append("  - URL : ").append(routeValide.getUrl()).append("\n")
                                .append("  - Méthode HTTP : ").append(routeValide.getMethod()).append("\n")
                                .append("  - Méthode : ").append(mapping.getMethodName()).append("\n\n");
            }
            
            throw new jakarta.servlet.ServletException(
                "URL introuvable : '" + url + "'.\nVoici les routes supportées par l'application :\n" + routesSupportees.toString()
            );
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