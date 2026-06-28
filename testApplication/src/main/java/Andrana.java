package testApplication.src.main.java;

import annotation.Controller;
import annotation.Mapping;

@Controller
public class Andrana {
    
   @Mapping(url = "/Andrana")
   public void andramo() {
        System.out.println("Test method in TestController1");
    }
}