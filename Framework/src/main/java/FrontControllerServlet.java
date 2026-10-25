package main.java;

import framework.utils.ModelView;
import framework.utils.Util;
import org.springframework.web.context.WebApplicationContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import jakarta.servlet.http.Part;
import framework.utils.Mapfile;
import framework.utils.Mysession;
import jakarta.servlet.http.HttpSession;


@MultipartConfig
public class FrontControllerServlet extends HttpServlet {
    private final HashMap<framework.utils.RouteKey, framework.utils.Mapping> urlMappings = new HashMap<>();
    private List<String> controllersList = new ArrayList<>();

    private String viewPrefix;
    private String uploadPath;
    private String authKey;   // attribut de session qui indique "connecte"
    private String loginUrl;  // page de connexion (optionnel)
    private String roleKey;   // attribut de session qui contient le(s) role(s)
    private final String SUFFIX = ".jsp";


    @Override
    public void init() throws ServletException {
        this.viewPrefix = getInitParameter("view-prefix");
        this.uploadPath = getInitParameter("upload-path");
        this.authKey = getInitParameter("auth-key");
        this.loginUrl = getInitParameter("login-url");
        this.roleKey = getInitParameter("role-key");
        if (this.authKey == null) {
            this.authKey = "user";
        }
        if (this.roleKey == null) {
            this.roleKey = "role";
        }

        if (this.viewPrefix == null) {
            this.viewPrefix = "/WEB-INF/views/";
        }
        if (this.uploadPath == null){
            this.uploadPath="C:/uploads";
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
                            urlMappings.put(routeKey, new framework.utils.Mapping(className, method.getName(),mappingAnnotation.json(), method.isAnnotationPresent(annotation.Auth.class),
                                method.isAnnotationPresent(annotation.Auth.class) ? method.getAnnotation(annotation.Auth.class).roles() : new String[0]));
                        }
                    }
                }
            } catch (Exception e) {
                throw new ServletException("Erreur scan package", e);
            }
        }
        System.out.println(uploadPath);
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

                // Sprint 10 : methode @Auth => il faut etre connecte
                if (mapping.isAuth() && !isAuthenticated(request)) {
                    if (loginUrl != null && !mapping.isJson()) {
                        response.sendRedirect(request.getContextPath() + loginUrl);
                    } else {
                        response.sendError(401, "Authentification requise : " + url);
                    }
                    return;
                }
                // Sprint 10 bis : connecte mais sans le bon role => 403
                if (mapping.isAuth() && mapping.getRoles().length > 0 && !hasRole(request, mapping.getRoles())) {
                    response.sendError(403, "Role insuffisant : " + url);
                    return;
                }
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

            Parameter[] params = method.getParameters();
Object[] args = new Object[params.length];
Mysession mysession = null;
for (int i = 0; i < params.length; i++) {
    Class<?> type = params[i].getType();
    if (type.equals(WebApplicationContext.class)) {
        if (springContext == null) {
            throw new ServletException("Pas de springContext disponible");
        }
        args[i] = springContext;
    } else if (type == HttpServletRequest.class) {
        args[i] = request;
    } else if (type == HttpSession.class) {
        args[i] = request.getSession();
    } else if (type == Mysession.class) {
        // une seule instance par requete, remplie depuis la HttpSession
        if (mysession == null) {
            mysession = new Mysession();
            mysession.load(request.getSession());
        }
        args[i] = mysession;
    } else if (type.isArray()) {
        String[] values = request.getParameterValues(params[i].getName());
        Class<?> elem = type.getComponentType();
        Object array = java.lang.reflect.Array.newInstance(elem, values == null ? 0 : values.length);
        for (int k = 0; values != null && k < values.length; k++) {
            java.lang.reflect.Array.set(array, k, convert(values[k], elem));
        }
            args[i] = array;
    } else if (List.class.isAssignableFrom(type)) {
        Class<?> elem = String.class;
        if (params[i].getParameterizedType() instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) params[i].getParameterizedType();
            elem = (Class<?>) pt.getActualTypeArguments()[0];
        }
        List<Object> list = new ArrayList<>();
        String name = params[i].getName();

        if (isSimple(elem)) {
            String[] values = request.getParameterValues(name);
            for (int k = 0; values != null && k < values.length; k++) {
                list.add(convert(values[k], elem));
            }
        } else {
            for (int k = 0; hasParameterStartingWith(request, name + "[" + k + "]."); k++) {
                list.add(bind(request, elem, name + "[" + k + "]."));
            }
        }
        args[i] = list;
        
    }else if (type == Mapfile.class) {
        Part part = request.getPart(params[i].getName());
        if(part != null && part.getSize() > 0){
        String nom = part.getSubmittedFileName();
        String extension = nom.substring(nom.lastIndexOf(".")+1);
        byte[] content = part.getInputStream().readAllBytes();

        Mapfile mapfile =new Mapfile(nom , extension , content);
        mapfile.save(uploadPath);
        args[i] = mapfile;
        }else{
            args[i]=null;
        }
    

    } else if (isSimple(type)) {
        args[i] = convert(request.getParameter(params[i].getName()), type);
    } else {
        args[i] = bind(request, type, "");
    }
}
Object o = method.invoke(instance, args);

// Apres l'invoke : reporter la Map Mysession dans la HttpSession
// (avant le forward vers la vue, pour que le JSP voie les valeurs)
if (mysession != null) {
    mysession.save(request.getSession());
}

            
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
private Object bind(HttpServletRequest request, Class<?> objectType, String prefix) throws Exception {
    Object object = objectType.getDeclaredConstructor().newInstance();

    for (java.lang.reflect.Field field : objectType.getDeclaredFields()) {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        if (isSimple(fieldType)) {
            String value = request.getParameter(prefix + field.getName());
            if (value == null || value.isEmpty()) continue;
            field.set(object, convert(value, fieldType));
        } else {
            String subPrefix = prefix + field.getName() + ".";
            if (hasParameterStartingWith(request, subPrefix)) {
                field.set(object, bind(request, fieldType, subPrefix));
            }
        }
    }
    return object;
}


// Connecte = la session existe deja et contient l'attribut authKey
// (getSession(false) : ne cree pas de session pour un visiteur anonyme)
private boolean isAuthenticated(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return session != null && session.getAttribute(authKey) != null;
}

// Vrai si le(s) role(s) de la session (String, tableau ou Collection)
// contiennent l'un des roles requis
private boolean hasRole(HttpServletRequest request, String[] required) {
    HttpSession session = request.getSession(false);
    Object value = session == null ? null : session.getAttribute(roleKey);
    if (value == null) return false;

    java.util.List<Object> owned = new ArrayList<>();
    if (value instanceof java.util.Collection) {
        owned.addAll((java.util.Collection<?>) value);
    } else if (value.getClass().isArray()) {
        for (int i = 0; i < java.lang.reflect.Array.getLength(value); i++) {
            owned.add(java.lang.reflect.Array.get(value, i));
        }
    } else {
        owned.add(value);
    }
    for (String r : required) {
        for (Object o : owned) {
            if (o != null && r.equals(o.toString())) return true;
        }
    }
    return false;
}

private boolean isSimple(Class<?> type) {
    return type == String.class || type.isPrimitive()
        || Number.class.isAssignableFrom(type) || type == Boolean.class;
}
private boolean hasParameterStartingWith(HttpServletRequest request, String prefix) {
    java.util.Enumeration<String> names = request.getParameterNames();
    while (names.hasMoreElements()) {
        if (names.nextElement().startsWith(prefix)) return true;
    }
    return false;
}


private Object convert(String value, Class<?> type) {
    boolean empty = (value == null || value.isEmpty());
    if (type == String.class) return value;
    if (type == int.class) return empty ? 0 : Integer.parseInt(value);
    if (type == Integer.class) return empty ? null : Integer.parseInt(value);
    if (type == long.class) return empty ? 0L : Long.parseLong(value);
    if (type == double.class) return empty ? 0.0 : Double.parseDouble(value);
    if (type == boolean.class) return !empty && Boolean.parseBoolean(value);
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