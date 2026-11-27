package main.java.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import main.java.repository.ProduitRepository;


@Configuration
public class AppConfig {

    private static final String DB_URL      = "jdbc:postgresql://localhost:5432/framework_db";
    private static final String DB_USER     = "postgres";
    private static final String DB_PASSWORD = "1234";

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(DB_URL);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASSWORD);
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public ProduitRepository produitRepository(JdbcTemplate jdbcTemplate) {
        return new ProduitRepository(jdbcTemplate);
    }
}
