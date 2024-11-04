package app.entities;

import app.dtos.TripDTO;
import app.entities.enums.Category;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String starttime;

    private String name;

    private String endtime;

    private String startposition;

    private double price;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    private Guide guide;

    public Trip(TripDTO tripDTO) {
        this.id = tripDTO.getId();
        this.category = tripDTO.getCategory();
        this.starttime = tripDTO.getStarttime();
        this.name = tripDTO.getName();
        this.endtime = tripDTO.getEndtime();
        this.startposition = tripDTO.getStartposition();
        this.price = tripDTO.getPrice();
        this.guide = tripDTO.getGuide();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(id, trip.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
