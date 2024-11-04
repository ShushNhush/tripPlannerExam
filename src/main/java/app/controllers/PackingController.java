package app.controllers;

import app.entities.enums.Category;
import app.services.PackingItems;
import app.services.dtos.PackingItemsDTO;
import io.javalin.http.Context;

public class PackingController {

    public void getPackingList(Context ctx) {

        try {
            Category category = Category.valueOf(ctx.pathParam("category"));
            PackingItemsDTO packingList = PackingItems.getInstance().getPackingItems(category);

            ctx.json(packingList);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
        }
    }
}
