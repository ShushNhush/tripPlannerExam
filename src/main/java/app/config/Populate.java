package app.config;

import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import jakarta.persistence.EntityManagerFactory;
import app.entities.enums.Category;

public class Populate {


    private static Populate instance;
    private static EntityManagerFactory emf;

    public static Populate getInstance(EntityManagerFactory emf_) {
        if (instance == null) {
            emf = emf_;
            instance = new Populate();
        }
        return instance;
    }


    public void populate() {

        // create trip and guide entities with some dummy data using the builder

        Trip trip1 = Trip.builder()
                .category(Category.CITY)
                .starttime("10:00")
                .name("Adventure trip")
                .endtime("18:00")
                .startposition("Athens")
                .price(100)
                .build();

        Trip trip2 = Trip.builder()
                .category(Category.SNOW)
                .starttime("09:00")
                .name("Mountain trip")
                .endtime("17:00")
                .startposition("Thessaloniki")
                .price(150)
                .build();

        Guide guide1 = Guide.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@doe")
                .phone("1234567890")
                .yearsOfExperience(5)
                .build();

        Guide guide2 = Guide.builder()
                .firstname("Jane")
                .lastname("Doe")
                .email("jane@doe")
                .phone("0987654321")
                .yearsOfExperience(10)
                .build();

        TripDAO tripDAO = TripDAO.getInstance(emf);

        tripDAO.create(new TripDTO(trip1));
        tripDAO.create(new TripDTO(trip2));

        GuideDAO guideDAO = GuideDAO.getInstance(emf);

        guideDAO.create(new GuideDTO(guide1));
        guideDAO.create(new GuideDTO(guide2));
    }

}
