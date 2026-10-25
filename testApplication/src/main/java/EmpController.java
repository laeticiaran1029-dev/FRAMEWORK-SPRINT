package main.java;

import java.util.List;

import annotation.Auth;
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

// Sprint 10 : connexion (la cle "user" est celle que lit le framework, cf. auth-key)
@Mapping(url = "/emp/loginForm", method = "GET")
public ModelView loginForm() {
    return new ModelView("loginForm");
}

@Mapping(url = "/emp/login", method = "POST")
public ModelView login(String login, Mysession session) {
    session.set("user", login);
    return new ModelView("profil");
}

// Sprint 10 : accessible seulement si connecte, sinon redirige vers login-url
@Auth
@Mapping(url = "/emp/profil", method = "GET")
public ModelView profil() {
    return new ModelView("profil");
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
