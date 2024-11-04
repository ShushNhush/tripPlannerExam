package app.routes;

import app.controllers.TripController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private final TripController tripController = new TripController();

    protected EndpointGroup getRoutes() {

        return () -> {
            get("/", tripController::getAll, Role.ANYONE);
            get("/{id}", tripController::getById, Role.ANYONE);
            get("/categories/{category}", tripController::getByCategory, Role.ANYONE);
            get("/totalWeight/{id}", tripController::sumOfWeightForTrip, Role.USER, Role.ADMIN);
            post("/", tripController::create, Role.ADMIN);
            put("/{id}", tripController::update, Role.ADMIN);
            delete("/{id}", tripController::delete, Role.ADMIN);
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip, Role.ADMIN);

        };
    }
}
