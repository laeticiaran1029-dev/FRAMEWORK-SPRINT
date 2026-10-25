package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Pose sur une methode d'action : elle ne s'execute que si l'utilisateur est connecte
// (la session contient l'attribut "auth-key" configure dans web.xml, "user" par defaut).
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {
}
