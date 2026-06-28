package testApplication.src.main.java;

import annotation.Controller;
import annotation.Mapping; 

@Controller 
public class TestController1 {
    
   @Mapping(url = "/test")
   public void testMethod() {
        System.out.println("Test method in TestController1");
    }
}