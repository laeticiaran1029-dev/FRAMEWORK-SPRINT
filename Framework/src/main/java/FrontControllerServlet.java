package main.java;

import framework.utils.ModelView;
import framework.utils.Util;
import org.springframework.web.context.WebApplicationContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;

public class FrontControllerServlet extends HttpServlet {
    private final HashMap<framework.utils.RouteKey, framework.utils.Mapping> urlMappings = new HashMap<>();
    private List<String> controllersList = new ArrayList<>();

    private String viewPrefix;
    private final String SUFFIX = ".jsp";

    @Override
    public void init() throws ServletException {
        this.viewPrefix = getInitParameter("view-prefix");
        if (this.viewPrefix == null) {
            this.viewPrefix = "/WEB-INF/views/";
        }

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
                            urlMappings.put(routeKey, new framework.utils.Mapping(className, method.getName(),mappingAnnotation.json()));
                        }
                    }
                }
            } catch (Exception e) {
                throw new ServletException("Erreur scan package", e);
            }
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String url = request.getRequestURI().substring(request.getContextPath().length());
        framework.utils.RouteKey routeKey = new framework.utils.RouteKey(url, request.getMethod());

        WebApplicationContext springContext =
            (WebApplicationContext) getServletContext().getAttribute("springContext");

        if (urlMappings.containsKey(routeKey)) {
            try {
                framework.utils.Mapping mapping = urlMappings.get(routeKey);
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method method = null;
                for (Method m : clazz.getDeclaredMethods()) {
                    if (m.getName().equals(mapping.getMethodName())) {
                        method = m;
                        break;
                    }
                }
                if (method == null) {
                    throw new ServletException("Methode introuvable : " + mapping.getMethodName());
                }

                Class<?>[] types = method.getParameterTypes();
Object[] args = new Object[types.length];
for (int i = 0; i < types.length; i++) {
    if (types[i].equals(WebApplicationContext.class)) {
        if (springContext == null) {
            throw new ServletException("Pas de springContext disponible");
        }
        args[i] = springContext;
    } else if (types[i] == HttpServletRequest.class) {
        args[i] = request;
    } else {
        args[i] = bind(request, types[i]);
    }
}
Object o = method.invoke(instance, args);

            
                if(mapping.isJson()){
                    response.setContentType("application/json;charset=UTF-8");
                    Gson gson = new Gson();
                    String json= gson.toJson(o);
                    out.println(json);
                }
                else if(o instanceof ModelView){
                      ModelView mv = (ModelView) o;
                    for (String key : mv.getData().keySet()) {
                        request.setAttribute(key, mv.getData().get(key));
                    }
                    String path = viewPrefix + mv.getView() + SUFFIX;
                    request.getRequestDispatcher(path).forward(request, response);
                    }
    
                else if (o != null) {
                    out.println("<h1>Succès : Affichage des résultats</h1>");
                    out.println("<p>URL appelée : <b>" + url + "</b></p>");
                    out.println("<p>Méthode HTTP : <b>" + request.getMethod() + "</b></p>");
                    out.println("<p>Classe : <b>" + mapping.getClassName() + "</b></p>");
                    out.println("<p>Fonction utilisée : <b>" + mapping.getMethodName() + "</b></p>");
                    out.println("<p>Résultat : " + o.toString() + "</p>");
                }
            } catch (Exception e) {
                out.println("Erreur d'exécution : " + e.getMessage());
                e.printStackTrace(out);
            }
        } else {
            response.sendError(404, "URL introuvable : " + url);
        }
    }
    private Object bind(HttpServletRequest request, Class<?> objectType) throws Exception {
    Object object = objectType.getDeclaredConstructor().newInstance();

    for (java.lang.reflect.Field field : objectType.getDeclaredFields()) {
        String value = request.getParameter(field.getName());
        if (value == null || value.isEmpty()) continue;

        field.setAccessible(true);
        field.set(object, convert(value, field.getType()));
    }
    return object;
}

private Object convert(String value, Class<?> type) {
    if (type == String.class) return value;
    if (type == int.class || type == Integer.class) return Integer.parseInt(value);
    if (type == long.class || type == Long.class) return Long.parseLong(value);
    if (type == double.class || type == Double.class) return Double.parseDouble(value);
    if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
    return null;
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