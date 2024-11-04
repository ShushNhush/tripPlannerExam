package app.routes;

import app.controllers.GuideController;
import app.controllers.PackingController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final TripRoutes tripRoutes = new TripRoutes();
    private final PackingController packingController = new PackingController();
    private final GuideRoutes guideRoutes = new GuideRoutes();
    public EndpointGroup getRoutes() {
        return () -> {

            path("/trips", tripRoutes.getRoutes());
            path("/packinglist", () -> {
                get("/{category}", packingController::getPackingList);
            });
            path("/guides", guideRoutes.getRoutes());
        };
    }
}
