package main.java;

import framework.utils.ModelView;
import annotation.Controller;
import annotation.Mapping;
import main.java.repository.ProduitRepository;
import org.springframework.web.context.WebApplicationContext;

@Controller
public class Andrana {

    @Mapping(url = "/test", method = "GET")
    public String test() {
        return "Page de test <br><br>"
        + "<a href='list'>Voir la liste des produits</a>";
    }
     @Mapping(url = "/api/test", method = "GET",json= true)
    public String apitest() {
        return "alefaa";
    }

    @Mapping(url = "/list", method = "GET")
    public ModelView list(WebApplicationContext springContext) {
        ModelView mv = new ModelView("index");

        ProduitRepository repo = springContext.getBean(ProduitRepository.class);
        mv.addItem("maListe", repo.findAllNoms());

        return mv;
    }

}