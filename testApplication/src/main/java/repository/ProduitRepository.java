package main.java.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Lit la table "produit" de la base framework_db.
 * Cette classe ne connait ni le framework ni les servlets : elle ne fait
 * que du SQL. C'est Spring qui lui fournit le JdbcTemplate.
 */
public class ProduitRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProduitRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retourne une ligne de texte par produit, pour que index.jsp
     * (qui attend un List<String>) fonctionne sans modification.
     */
    public List<String> findAllNoms() {
        return jdbcTemplate.query(
            "SELECT nom, prix FROM produit ORDER BY id",
            (rs, rowNum) -> rs.getString("nom") + " - " + rs.getBigDecimal("prix") + " Ar"
        );
    }
}
