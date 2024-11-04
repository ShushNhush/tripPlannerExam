package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.enums.Category;
import app.exceptions.ErrorResponse;
import app.services.PackingItems;
import app.services.dtos.PackingItemsDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TripController {

    private EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory(false);
    private TripDAO dao = TripDAO.getInstance(emf);
    private GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);


    public void getAll(Context ctx) {
        logger.info("Received request to get all trips");

        try {
            ctx.json(dao.getAll());
            ctx.status(200);
            logger.info("Successfully retrieved all trips");

        } catch (EntityNotFoundException e) {
            // TODO: add check for 0 found in DAO layer to hit this exception
            logger.warn("No trips found: {}", e.getMessage());
            ctx.status(404);
            ctx.json(new ErrorResponse(404, e.getMessage()));

        } catch (Exception e) {
            logger.error("Error occurred while retrieving trips", e);
            ctx.status(500);
            ctx.json(new ErrorResponse(500, "Server Error"));
        }
    }

    public void getById(Context ctx)  {

        logger.info("Received request to get trip by id");
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO trip = dao.getById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("trip", trip);
            if (trip.getGuide() != null) {
                GuideDTO guide = guideDAO.getById(trip.getGuide().getId());
                response.put("guide", guide);
            }
            response.put("packingList", getPackingList(trip.getCategory()));
            ctx.json(response);
            ctx.status(200);
            logger.info("Successfully retrieved trip by id");
        } catch (EntityNotFoundException e) {
            logger.warn("Trip not found: {}", e.getMessage());
            ctx.status(404);
            ctx.json(new ErrorResponse(404, e.getMessage()));
        } catch (NumberFormatException e) {
            logger.warn("Bad request, invalid input");
            ctx.status(400);
            ctx.json(new ErrorResponse(400, "Bad Request"));
        }
    }

    public void create(Context ctx) {

        logger.info("Received request to create a trip");
        try {
            TripDTO trip = ctx.bodyAsClass(TripDTO.class);
            ctx.json(dao.create(trip));
            ctx.status(201);
            logger.info("Successfully created trip");
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request, invalid input");
            ctx.status(400);
            ctx.json(new ErrorResponse(400, "Bad Request, Failed to create trip, invalid input"));
        }catch (Exception e) {
            logger.error("Error occurred while creating trip", e);
            ctx.status(500);
            ctx.json(new ErrorResponse(500, "Server Error"));
        }
    }

    public void update(Context ctx) {

        logger.info("Received request to update trip");
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO trip = ctx.bodyAsClass(TripDTO.class);
            ctx.json(dao.update(id, trip));
            ctx.status(200);
            logger.info("Successfully updated trip");
        } catch (EntityNotFoundException e) {
            logger.warn("Trip not found: {}", e.getMessage());
            ctx.status(404);
            ctx.json(new ErrorResponse(404, e.getMessage()));
        } catch (NumberFormatException e) {
            logger.warn("Bad request, invalid input");
            ctx.status(400);
            ctx.json(new ErrorResponse(400, "Bad Request"));
        } catch (Exception e) {
            logger.error("Error occurred while updating trip", e);
            ctx.status(500);
            ctx.json(new ErrorResponse(500, "Server Error"));
        }
    }

    public void delete(Context ctx) {

        logger.info("Received request to delete trip");
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            dao.delete(id);
            ctx.status(200);
            ctx.json("Successfully deleted trip with id: " + id);
            logger.info("Successfully deleted trip");
        } catch (EntityNotFoundException e) {
            logger.warn("Trip not found: {}", e.getMessage());
            ctx.status(404);
            ctx.json(new ErrorResponse(404, e.getMessage()));
        } catch (NumberFormatException e) {
            logger.warn("Bad request, invalid input");
            ctx.status(400);
            ctx.json(new ErrorResponse(400, "Bad Request"));
        } catch (Exception e) {
            logger.error("Error occurred while deleting trip", e);
            ctx.status(500);
            ctx.json(new ErrorResponse(500, "Server Error"));
        }
    }

    public void addGuideToTrip(Context ctx) {

        logger.info("Received request to add guide to trip");
        try {
            int tripId = Integer.parseInt(ctx.pathParam("tripId"));
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            dao.addGuideToTrip(tripId, guideId);
            ctx.json("Successfully added guide to trip");
            ctx.status(200);
            logger.info("Successfully added guide to trip");
        } catch (EntityNotFoundException e) {
            logger.warn("Trip or guide not found: {}", e.getMessage());
            ctx.status(404);
            ctx.json(new ErrorResponse(404, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error occurred while adding guide to trip", e);
            ctx.status(500);
            ctx.json(new ErrorResponse(500, "Server Error"));
        }
    }

    public void getByCategory(Context ctx) {

        logger.info("Received request to get trips by category");
        try {
            Category category = Category.valueOf(ctx.pathParam("category"));
            ctx.json(dao.getTripsByCategory(category));
            ctx.status(200);
            logger.info("Successfully retrieved trips by category");
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid category");
            ctx.status(400);
            ctx.json(new ErrorResponse(400, "Invalid category"));
        } catch (EntityNotFoundException e) {
            logger.warn("No trips found");
            ctx.status(404);
            ctx.json(new ErrorResponse(404, "No trips found"));
        } catch (Exception e) {
            logger.error("Error occurred while retrieving trips by category", e);
            ctx.status(500);
            ctx.json(new ErrorResponse(500, "Server Error"));
        }
    }

    private PackingItemsDTO getPackingList(Category category) {
        return PackingItems.getInstance().getPackingItems(category);
    }

    public void sumOfWeightForTrip(Context ctx) {

        logger.info("Received request to get sum of weight for trip");
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));

            TripDTO trip = dao.getById(id);

            PackingItemsDTO packingList = getPackingList(trip.getCategory());

            int sum = packingList.getTotalWeight();

            ctx.json("Total weight for trip with id " + id + " is: " + sum + " grams");

            ctx.status(200);
            logger.info("Successfully retrieved sum of weight for trip");
        } catch (EntityNotFoundException e) {
            logger.warn("Trip not found: {}", e.getMessage());
            ctx.status(404);
            ctx.json(new ErrorResponse(404, e.getMessage()));
        } catch (NumberFormatException e) {
            logger.warn("Bad request, invalid input");
            ctx.status(400);
            ctx.json(new ErrorResponse(400, "Bad Request"));
        } catch (Exception e) {
            logger.error("Error occurred while retrieving sum of weight for trip", e);
            ctx.status(500);
            ctx.json(new ErrorResponse(500, "Server Error"));
        }
    }
}
