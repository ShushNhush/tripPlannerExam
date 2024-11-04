package app.services.dtos;

import lombok.Data;

@Data
public class BuyingOptionDTO {
    private String shopName;
    private String shopUrl;
    private double price; // Assuming price is a double, adjust as needed
}

