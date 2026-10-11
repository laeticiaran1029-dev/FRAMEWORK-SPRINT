package main.java;

import java.util.List;

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

    @Mapping(url = "/emp/info", method = "GET")
    public ModelView info(String nom, String prenom,int age) {
    ModelView mv = new ModelView("empResult");
    mv.addItem("nom", nom);
    mv.addItem("age", age);
    mv.addItem("prenom", prenom);
    return mv;
}

@Mapping(url = "/emp/liste", method = "POST")
public ModelView liste(List<Emp> emps) {
    ModelView mv = new ModelView("empResult");
    mv.addItem("emps", emps);
    return mv;
}


@Mapping(url = "/emp/detail", method = "POST")
public ModelView detail(Emp emp) {
    ModelView mv = new ModelView("empResult");
    mv.addItem("emp", emp);
    return mv;
}


}
