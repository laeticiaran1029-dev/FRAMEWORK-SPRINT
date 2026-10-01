package framework.utils;

import java.lang.reflect.Method;

public class Util {

    public static boolean haveParameter(Method methode, Class<?> param) {
        for (Class<?> type : methode.getParameterTypes()) {
            if (type.equals(param)) {
                return true;
            }
        }
        return false;
    }
}
