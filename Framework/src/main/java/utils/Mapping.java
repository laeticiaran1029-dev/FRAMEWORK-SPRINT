package framework.utils;

public class Mapping {
    private final String className;
     private final String methodName;
     private final boolean isJson;

    public Mapping(String className, String methodName, boolean isJson) {
        this.className = className;
        this.methodName = methodName;
        this.isJson = isJson;
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
}