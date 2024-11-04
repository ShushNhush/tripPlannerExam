package app.restTest;

import app.config.AppConfig;
import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.enums.Category;
import app.security.controllers.SecurityController;
import app.security.daos.SecurityDAO;
import app.security.dtos.UserDTO;
import app.security.exceptions.ValidationException;
import io.javalin.Javalin;
import io.restassured.common.mapper.TypeRef;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.proxy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestAssuredTest {

    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory(true);
    private final static SecurityController securityController = SecurityController.getInstance();
    private final static SecurityDAO securityDAO = new SecurityDAO(emf);
    private final static TripDAO tripDAO = TripDAO.getInstance(emf);
    private final static GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private static Javalin app;
    private static UserDTO userDTO, adminDTO;
    private static String userToken, adminToken;
    private static final String BASE_URL = "http://localhost:7070/api";


    @BeforeAll
    static void setUpAll() {

        app = AppConfig.startServer(7070);


    }


    @BeforeEach
    void setUp() {


        List<TripDTO> trips = Populator.populateTrips(emf);

        GuideDTO[] guides = Populator.populateGuides(emf);


        UserDTO[] users = Populator.populateUsers(emf);
        userDTO = users[0];
        adminDTO = users[1];

        try {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), userDTO.getPassword());
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getUsername(), adminDTO.getPassword());
            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Trip").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();


            em.createNativeQuery("ALTER SEQUENCE trip_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void readAll() {
        System.out.println("usertoken: " + userToken);
        System.out.println("admintoken: " + adminToken);

        List<TripDTO> tripDTOS =
                given()
                        .when()
                        .header("Authorization", userToken)
                        .get(BASE_URL + "/trips")
                        .then()
                        .statusCode(200)
                        .body("size()", is(2))
                        .log().all()
                        .extract()
                        .as(new TypeRef<List<TripDTO>>() {
                        });

        assertThat(tripDTOS.size(), is(2));
        assertThat(tripDTOS.get(0).getName(), is("test trip 2"));
        assertThat(tripDTOS.get(1).getName(), is("test trip 1"));
    }

    @Test
    @Order(2)
    void createTrip() {
        TripDTO newTrip = TripDTO.builder()
                .category(Category.SNOW)
                .starttime("2024-11-04T09:00:00")
                .name("Mountain Adventure")
                .endtime("2024-11-04T17:00:00")
                .startposition("Mountain Base Camp")
                .price(150.0)
                .build();

        TripDTO createdTrip =
                given()
                        .contentType("application/json")
                        .header("Authorization", adminToken)
                        .body(newTrip)
                        .when()
                        .post(BASE_URL + "/trips")
                        .then()
                        .statusCode(201)
                        .log().all()
                        .extract()
                        .as(TripDTO.class);


        assertThat(createdTrip.getName(), is("Mountain Adventure"));
        assertThat(createdTrip.getCategory(), is(Category.SNOW));
        assertThat(createdTrip.getPrice(), is(150.0));

    }

}
