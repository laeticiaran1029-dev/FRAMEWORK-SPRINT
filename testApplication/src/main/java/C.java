package testApplication.src.main.java;

import annotation.Controller;
import annotation.Mapping;

@Controller
public class C {
    
   @Mapping(url = "/C")
   public void oatra() {
        System.out.println("Test method in TestController1");
    }
}