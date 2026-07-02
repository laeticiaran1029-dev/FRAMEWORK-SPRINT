package testApplication.src.main.java;

import annotation.Controller;
import annotation.Mapping; 

@Controller 
public class B {
    
    @Mapping(url = "/test", method = "POST")
    public void methodeB() {
        System.out.println("Test method in TestController1");
    }
}