//package app.daos;
//
//import app.config.HibernateConfig;
//import app.dtos.PlantDTO;
//import app.dtos.ResellerDTO;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//
//class PlantDAOTest {
//
//    private static EntityManagerFactory emf;
//    private static PlantDAO plantDAO;
//    private static PlantDTO plant1 = new PlantDTO("Plant 1", "Type 1", 10.0, 10);
//    private static PlantDTO plant2 = new PlantDTO("Plant 2", "Type 2", 20.0, 20);
//    private static ResellerDTO reseller1 = new ResellerDTO("Reseller 1", "Address 1", "Phone 1");
//
//    @BeforeAll
//    static void setUp() {
//
//        emf = HibernateConfig.getEntityManagerFactory(true);
//
//        plantDAO = PlantDAO.getInstance(emf);
//
//    }
//
//    @BeforeEach
//    void setUpEach() {
//
//        plantDAO.create(plant1);
//
//    }
//
//    @AfterEach
//    void tearDown() {
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            em.createQuery("DELETE FROM Plant").executeUpdate();
//
//            // Reset sequences
//            em.createNativeQuery("ALTER SEQUENCE plant_id_seq RESTART WITH 1").executeUpdate();
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    void create() {
//        plantDAO.create(plant2);
//        int actual = plantDAO.getAll().size();
//        int expected = 2;
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void getAll() {
//        int actual = plantDAO.getAll().size();
//        int expected = 1;
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void getById() {
//        PlantDTO actual = plantDAO.getById(1);
//        plant1.setId(1);
//        PlantDTO expected = plant1;
//        assertEquals(expected, actual);
//
//    }
//
//}