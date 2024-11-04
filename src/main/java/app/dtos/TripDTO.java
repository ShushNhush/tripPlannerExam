package app.dtos;

import app.entities.Guide;
import app.entities.Trip;
import app.entities.enums.Category;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripDTO {

    private Integer id;

    private Category category;

    private String starttime;

    private String name;

    private String endtime;

    private String startposition;

    private double price;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Guide guide;

    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.category = trip.getCategory();
        this.starttime = trip.getStarttime();
        this.name = trip.getName();
        this.endtime = trip.getEndtime();
        this.startposition = trip.getStartposition();
        this.price = trip.getPrice();
        this.guide = trip.getGuide();
    }
}
