package framework.utils;

public class Mapping {
    private final String className;
     private final String methodName;
     private final boolean isJson;
     private final boolean auth;
     private final String[] roles;

    public Mapping(String className, String methodName, boolean isJson) {
        this(className, methodName, isJson, false, new String[0]);
    }

    public Mapping(String className, String methodName, boolean isJson, boolean auth, String[] roles) {
        this.className = className;
        this.methodName = methodName;
        this.isJson = isJson;
        this.auth = auth;
        this.roles = roles;
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

    public String[] getRoles(){
        return roles;
    }
}
