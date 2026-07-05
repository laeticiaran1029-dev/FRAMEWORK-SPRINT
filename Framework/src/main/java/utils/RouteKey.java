package main.java.utils;

import java.util.Objects;

public class RouteKey {
    private final String url;
    private final String method;

    public RouteKey(String url, String method) {
        this.url = url;
        this.method = method == null ? "GET" : method.toUpperCase();
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        RouteKey routeKey = (RouteKey) object;
        return Objects.equals(url, routeKey.url) && Objects.equals(method, routeKey.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {
        return method + " " + url;
    }
}