package framework.utils;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

// Session vue par le developpeur : une simple Map<String, Object>.
// Le framework la remplit depuis la HttpSession avant l'appel de la methode d'action
// (load) et reporte les modifications dans la HttpSession apres l'appel (save).
public class Mysession {

    private Map<String, Object> data = new HashMap<>();
    private boolean invalidated = false;

    public Object get(String key) {
        return data.get(key);
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public void remove(String key) {
        data.remove(key);
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    public Map<String, Object> getData() {
        return data;
    }

    // Demande la destruction de la session (logout) ; appliquee par save().
    public void invalidate() {
        data.clear();
        invalidated = true;
    }

    // HttpSession -> Map
    public void load(HttpSession session) {
        data.clear();
        java.util.Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            data.put(name, session.getAttribute(name));
        }
    }

    // Map -> HttpSession
    public void save(HttpSession session) {
        if (invalidated) {
            session.invalidate();
            return;
        }
        // retire les attributs supprimes dans la Map
        java.util.List<String> existing = java.util.Collections.list(session.getAttributeNames());
        for (String name : existing) {
            if (!data.containsKey(name)) {
                session.removeAttribute(name);
            }
        }
        for (Map.Entry<String, Object> e : data.entrySet()) {
            session.setAttribute(e.getKey(), e.getValue());
        }
    }
}
