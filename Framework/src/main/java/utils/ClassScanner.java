package framework.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import annotation.Controller;

public class ClassScanner {

    // Méthode statique qui renvoie la liste des noms de classes annotées
    public static List<String> findControllers(String packageName) throws Exception {
        List<String> controllerList = new ArrayList<>();
        
        // Transformer le nom du package en chemin de dossier (ex: testApplication.controllers -> testApplication/controllers)
        String path = packageName.replace('.', '/');
        
        // Demander au ClassLoader de trouver ce dossier physique sur le disque
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        
        if (resource == null) {
            throw new IllegalArgumentException("Package introuvable : " + packageName);
        }

        //  Récupérer le dossier
        File directory = new File(resource.getFile());

        // Parcourir tous les fichiers du dossier
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            
            for (File file : files) {
                // On ne s'intéresse qu'aux fichiers compilés (.class)
                if (file.getName().endsWith(".class")) {
                    
                    // Récupérer le nom de la classe sans l'extension ".class"
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    String fullClassName = packageName + "." + className;
                    
                    // Charger la classe en mémoire
                    Class<?> clazz = Class.forName(fullClassName);
                    
                    //  LA VÉRIFICATION : Est-ce que cette classe a l'annotation @Controller ?
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        controllerList.add(clazz.getName());
                    }
                }
            }
        }
        
        return controllerList;
    }
}