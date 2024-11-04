package app;

import app.config.AppConfig;
import app.config.HibernateConfig;
import app.config.Populate;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {

        AppConfig.startServer(7070);
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory(false);
        Populate.getInstance(emf).populate();

        // TODO: Change DB name in config.properties file
        // TODO: Create a DB before the program can run
    }
}