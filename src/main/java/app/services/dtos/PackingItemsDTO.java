package app.services.dtos;

import lombok.Data;

import java.util.List;

@Data
public class PackingItemsDTO {
    private List<ItemDTO> items;

    public int getTotalWeight() {
        return items.stream()
                .mapToInt(ItemDTO::getWeightInGrams) // Map each item to its weight
                .sum();
    }
}
