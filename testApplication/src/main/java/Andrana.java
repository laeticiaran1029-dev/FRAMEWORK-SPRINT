package main.java;

import framework.utils.ModelView;
import annotation.Controller;
import annotation.Mapping;
import java.util.Arrays;
import java.util.List;

@Controller
public class Andrana {

    @Mapping(url = "/test", method = "GET")
    public String test() {
        return "Page de test <br><br>"
        + "<a href='list'>Voir la liste des produits</a>";
    }

    @Mapping(url = "/list", method = "GET")
    public ModelView list() {
        ModelView mv = new ModelView("index");

        List<String> maListeEnDur = Arrays.asList("kaka", "Frites", "Boisson");
        mv.addItem("maListe", maListeEnDur);

        return mv;
    }
}