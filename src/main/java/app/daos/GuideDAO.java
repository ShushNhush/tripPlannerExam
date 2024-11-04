package app.daos;

import app.daos.interfaces.IDAO;
import app.dtos.GuideDTO;
import app.entities.Guide;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GuideDAO implements IDAO<GuideDTO, Integer> {


    private static GuideDAO instance;
    private static EntityManagerFactory emf;

    public static GuideDAO getInstance(EntityManagerFactory emf_) {
        if (instance == null) {
            emf = emf_;
            instance = new GuideDAO();
        }
        return instance;
    }

    @Override
    public Set<GuideDTO> getAll() {

        try (var em = emf.createEntityManager()) {

            TypedQuery<Guide> query = em.createQuery("SELECT g FROM Guide g", Guide.class);

            Set<GuideDTO> guideDTOS = query.getResultList().stream().map(GuideDTO::new).collect(Collectors.toSet());

            return guideDTOS;
        }
    }

    @Override
    public GuideDTO getById(Integer id) {

        try (var em = emf.createEntityManager()) {

            Guide guide = em.find(Guide.class, id);

            if (guide == null) {
                return null;
            }
            return new GuideDTO(guide);
        }
    }

    @Override
    public GuideDTO create(GuideDTO t) {

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = new Guide(t);
            em.persist(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        }
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO t) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GuideDTO delete(Integer id) {
        return null;
    }

    public List<Map<String, Object>> profitPerGuide() {
        try (var em = emf.createEntityManager()) {
            // The query to get each guide with the sum of trip prices
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT g.id, COALESCE(SUM(t.price), 0) FROM Guide g LEFT JOIN g.trips t GROUP BY g.id",
                    Object[].class);

            List<Object[]> results = query.getResultList();

            // Transform the results into the desired format
            return results.stream()
                    .map(result -> {
                        Map<String, Object> guideProfit = new HashMap<>();
                        guideProfit.put("guideId", result[0]); // guide ID
                        guideProfit.put("totalPrice", result[1]); // total price
                        return guideProfit;
                    })
                    .collect(Collectors.toList());
        }
    }

}
