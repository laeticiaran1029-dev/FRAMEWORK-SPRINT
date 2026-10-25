package framework.utils;

public class Mapping {
    private final String className;
     private final String methodName;
     private final boolean isJson;
     private final boolean auth;

    public Mapping(String className, String methodName, boolean isJson) {
        this(className, methodName, isJson, false);
    }

    public Mapping(String className, String methodName, boolean isJson, boolean auth) {
        this.className = className;
        this.methodName = methodName;
        this.isJson = isJson;
        this.auth = auth;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;

    }

    public boolean isJson(){
        return isJson;
    }

    public boolean isAuth(){
        return auth;
    }
}
