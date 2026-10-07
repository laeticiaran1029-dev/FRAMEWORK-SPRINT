package main.java;

import annotation.Controller;
import annotation.Mapping;
import config.model.Emp;
import framework.utils.ModelView;

@Controller
public class EmpController {

    @Mapping(url = "/emp/form")
    public ModelView form() {
        return new ModelView("empForm");
    }

    @Mapping(url = "/emp/save", method = "POST")
    public void save(Emp emp) {
        System.out.println(emp.getNom() + " " + emp.getPrenom() + " " + emp.getAge());
    }
}
