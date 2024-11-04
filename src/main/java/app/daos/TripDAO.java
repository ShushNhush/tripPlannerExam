package app.daos;

import app.daos.interfaces.IDAO;
import app.daos.interfaces.ITripGuideDAO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.entities.enums.Category;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.Set;

public class TripDAO implements IDAO<TripDTO, Integer>, ITripGuideDAO {


    private static TripDAO instance;
    private static EntityManagerFactory emf;

    public static TripDAO getInstance(EntityManagerFactory emf_) {
        if (instance == null) {
            emf = emf_;
            instance = new TripDAO();
        }
        return instance;
    }

    @Override
    public Set<TripDTO> getAll() {

        try (var em = emf.createEntityManager()) {

            TypedQuery<Trip> query = em.createQuery
                    ("SELECT t FROM Trip t LEFT JOIN FETCH t.guide g LEFT JOIN FETCH g.trips",
                            Trip.class);

            Set<TripDTO> tripDTOS = query.getResultList().stream().map(TripDTO::new).collect(java.util.stream.Collectors.toSet());

            return tripDTOS;

        }
    }

    @Override
    public TripDTO getById(Integer id) {

        try (var em = emf.createEntityManager()) {

            Trip trip = em.find(Trip.class, id);

            if (trip == null) {
                throw new EntityNotFoundException("Trip with id " + id + " not found");
            }
            return new TripDTO(trip);
        }
    }

    @Override
    public TripDTO create(TripDTO t) {

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = new Trip(t);
            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public TripDTO update(Integer id, TripDTO t) {

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new EntityNotFoundException("Trip with id " + id + " not found");
            }
            trip.setCategory(t.getCategory());
            trip.setStarttime(t.getStarttime());
            trip.setName(t.getName());
            trip.setEndtime(t.getEndtime());
            trip.setStartposition(t.getStartposition());
            trip.setPrice(t.getPrice());
            trip.setGuide(t.getGuide());
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public TripDTO delete(Integer id) {

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new EntityNotFoundException("Trip with id " + id + " not found");
            }
            em.remove(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public void addGuideToTrip(int tripId, int guideId) {
        try (var em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, tripId);
            Guide guide = em.find(Guide.class, guideId);

            if (trip == null) {
                throw new EntityNotFoundException("Trip with ID " + tripId + " not found");
            }
            if (guide == null) {
                throw new EntityNotFoundException("Guide with ID " + guideId + " not found");
            }

            try {
                em.getTransaction().begin();

                trip.setGuide(guide);
                guide.getTrips().add(trip);

                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Error adding guide to trip", e);
            }
        }
    }

    @Override
    public Set<TripDTO> getTripsByGuide(int guideId) {

        try (var em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.guide.id = :guideId", Trip.class);
            query.setParameter("guideId", guideId);
            return query.getResultList().stream().map(TripDTO::new).collect(java.util.stream.Collectors.toSet());
        }
    }

    // make a method to filter trips by category

    public Set<TripDTO> getTripsByCategory(Category category) {
        try (var em = emf.createEntityManager()) {

            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.category = :category", Trip.class);
            query.setParameter("category", category);
            return query.getResultList().stream().map(TripDTO::new).collect(java.util.stream.Collectors.toSet());
        }
    }
}
