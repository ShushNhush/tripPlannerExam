package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

public class GuideController {

    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory(false);
    private static GuideDAO dao = GuideDAO.getInstance(emf);


    public void guideProfits(Context ctx) {

        ctx.json(dao.profitPerGuide());
        ctx.status(200);

    }

}
