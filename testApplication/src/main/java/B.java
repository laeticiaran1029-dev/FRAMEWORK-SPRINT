package main.java;

import annotation.Controller;
import annotation.Mapping;

@Controller
public class B {
    
    @Mapping(url = "/B", method = "GET")
    public String methodeBGet() {
        return "GET /B";
    }

    @Mapping(url = "/B", method = "POST")
    public String methodeB() {
        return "POST /B";
    }
}