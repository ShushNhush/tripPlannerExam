package app.services;

import app.entities.enums.Category;
import app.services.dtos.PackingItemsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PackingItems {

    private static ObjectMapper objectMapper = new ObjectMapper();


    private static PackingItems instance;

    public static PackingItems getInstance() {
        if (instance == null) {
            instance = new PackingItems();
        }
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return instance;
    }

    public PackingItemsDTO getPackingItems(Category category) {
        try {
            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            String categoryValue = category.toString().toLowerCase();

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://packingapi.cphbusinessapps.dk/packinglist/" + categoryValue))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code
            if (response.statusCode() == 200) {
                // Deserialize the response into PackingItemsDTO
                System.out.println(category);
                return objectMapper.readValue(response.body(), PackingItemsDTO.class);
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
                return null; // Or throw an exception if you prefer
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Or throw an exception
        }
    }
}

