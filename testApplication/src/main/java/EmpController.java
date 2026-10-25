package main.java;

import java.util.List;

import annotation.Controller;
import annotation.Mapping;
import config.model.Emp;
import framework.utils.Mapfile;
import framework.utils.ModelView;
import framework.utils.Mysession;


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
public ModelView liste(List<Emp> emps,Mapfile photo, Mysession session) {
    // range le nom du dernier employe insere dans la session
    if (emps != null && !emps.isEmpty()) {
        session.set("dernierEmp", emps.get(emps.size() - 1).getNom());
    }
    ModelView mv = new ModelView("empResult");
    mv.addItem("emps", emps);
    mv.addItem("photo", photo);
    if (photo != null) {
    System.out.println(photo.getFileName());
    System.out.println(photo.getContent().length);
}

    
    return mv;

}


// Relecture : la valeur rangee par liste() survit a la requete suivante
@Mapping(url = "/emp/dernier", method = "GET")
public ModelView dernier(Mysession session) {
    // la vue lit la valeur via session.getAttribute("dernierEmp")
    return new ModelView("empResult");
}

// Logout : detruit la session
@Mapping(url = "/emp/logout", method = "GET")
public ModelView logout(Mysession session) {
    session.invalidate();
    return new ModelView("empResult");
}

@Mapping(url = "/emp/detail", method = "POST")
public ModelView detail(Emp emp) {
    ModelView mv = new ModelView("empResult");
    mv.addItem("emp", emp);
    return mv;
}


}
