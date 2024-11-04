package app.restTest;

import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.entities.enums.Category;
import app.security.dtos.UserDTO;
import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class Populator {

    public static GuideDTO[] populateGuides(EntityManagerFactory emf) {

        Guide guide1 = Guide.builder()
                .firstname("test guide 1")
                .lastname("Doe")
                .email("john@doe")
                .phone("1234567890")
                .yearsOfExperience(5)
                .build();

        Guide guide2 = Guide.builder()
                .firstname("test guide 2")
                .lastname("Doe")
                .email("jane@doe")
                .phone("0987654321")
                .yearsOfExperience(10)
                .build();
        GuideDAO guideDAO = GuideDAO.getInstance(emf);

        guideDAO.create(new GuideDTO(guide1));
        guideDAO.create(new GuideDTO(guide2));

        return new GuideDTO[]{new GuideDTO(guide1), new GuideDTO(guide2)};
    }

    public static List<TripDTO> populateTrips(EntityManagerFactory emf) {
        Trip trip1 = Trip.builder()
                .category(Category.CITY)
                .starttime("10:00")
                .name("test trip 1")
                .endtime("18:00")
                .startposition("Athens")
                .price(100)
                .build();

        Trip trip2 = Trip.builder()
                .category(Category.SNOW)
                .starttime("09:00")
                .name("test trip 2")
                .endtime("17:00")
                .startposition("Thessaloniki")
                .price(150)
                .build();

        TripDAO tripDAO = TripDAO.getInstance(emf);

        // Create a list to hold the TripDTO objects
        List<TripDTO> trips = new ArrayList<>();

        // Create and add TripDTO objects to the list
        trips.add(new TripDTO(trip1));
        trips.add(new TripDTO(trip2));

        // Persist the trips
        tripDAO.create(new TripDTO(trip1)); // Persist trip1
        tripDAO.create(new TripDTO(trip2)); // Persist trip2

        // Return the list of TripDTOs
        return trips;
    }

    public static UserDTO[] populateUsers(EntityManagerFactory emf) {
        User user, admin;
        Role userRole, adminRole;

        user = new User("usertest", "user123");
        admin = new User("admintest", "admin123");
        userRole = new Role("USER");
        adminRole = new Role("ADMIN");
        user.addRole(userRole);
        admin.addRole(adminRole);

        try (var em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }
        UserDTO userDTO = new UserDTO(user.getUsername(), "user123");
        UserDTO adminDTO = new UserDTO(admin.getUsername(), "admin123");
        return new UserDTO[]{userDTO, adminDTO};
    }
}
