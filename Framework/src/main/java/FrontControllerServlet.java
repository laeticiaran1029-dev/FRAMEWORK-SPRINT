package main.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import main.java.annotation.Mapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public class FrontControllerServlet extends HttpServlet {
    private final HashMap<main.java.utils.RouteKey, main.java.utils.Mapping> urlMappings = new HashMap<>();
    private List<String> controllersList = new ArrayList<>();
    
    @Override
    public void init() throws ServletException {
        String packageToScan = getInitParameter("controller-package");
        
        if (packageToScan != null) {
            try {
                this.controllersList = main.java.utils.ClassScanner.findControllers(packageToScan);
                for (String className : controllersList) {
                    Class<?> clazz = Class.forName(className);
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Mapping.class)) {
                            Mapping mappingAnnotation = method.getAnnotation(Mapping.class);
                            main.java.utils.RouteKey routeKey = new main.java.utils.RouteKey(mappingAnnotation.url(), mappingAnnotation.method());
                            if (urlMappings.containsKey(routeKey)) {
                                throw new ServletException("Route déjà déclarée : " + routeKey);
                            }
                            urlMappings.put(routeKey, new main.java.utils.Mapping(className, method.getName()));
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
        main.java.utils.RouteKey routeKey = new main.java.utils.RouteKey(url, request.getMethod());

        if (urlMappings.containsKey(routeKey)) {
            try {
                main.java.utils.Mapping mapping = urlMappings.get(routeKey);
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method method = clazz.getDeclaredMethod(mapping.getMethodName());
                Object result = method.invoke(instance);

                out.println("<h1>Contrôleur invoqué</h1>");
                out.println("<p>Classe : <b>" + mapping.getClassName() + "</b></p>");
                out.println("<p>&nbsp;&nbsp; - URL : <b>" + url + "</b></p>");
                out.println("<p>&nbsp;&nbsp; - Méthode HTTP : <b>" + routeKey.getMethod() + "</b></p>");
                out.println("<p>&nbsp;&nbsp; - Méthode utilisée : <b>" + mapping.getMethodName() + "</b></p>");
                out.println("<p>&nbsp;&nbsp; - Résultat : <b>" + (result == null ? "Aucun retour" : result.toString()) + "</b></p>");
                
            } catch (Exception e) {
                out.println("Erreur d'exécution : " + e.getMessage());
            }
        } else {
            StringBuilder routesSupportees = new StringBuilder();
            routesSupportees.append("\n");
            
            for (java.util.Map.Entry<main.java.utils.RouteKey, main.java.utils.Mapping> entry : urlMappings.entrySet()) {
                main.java.utils.RouteKey routeValide = entry.getKey();
                main.java.utils.Mapping mapping = entry.getValue();
                
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