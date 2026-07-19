package main.java;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Declare dans le web.xml de l'application (et non par @WebListener),
 * pour garantir qu'il s'execute APRES le ContextLoaderListener de Spring.
 * Sinon le contexte Spring ne serait pas encore publie et on lirait null.
 */
public class AppListener implements ServletContextListener {

    private static final String SPRING_ROOT = "org.springframework.web.context.WebApplicationContext.ROOT";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("springContext", servletContext.getAttribute(SPRING_ROOT));
    }
}
