package app.routes;

import app.controllers.GuideController;
import app.controllers.TripController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GuideRoutes {


    private final GuideController guideController = new GuideController();

    protected EndpointGroup getRoutes() {

        return () -> {
            get("/profits", guideController::guideProfits);
        };
    }
}
