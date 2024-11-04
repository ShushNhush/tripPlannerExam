package app.dtos;

import app.entities.Guide;
import app.entities.Trip;
import app.entities.enums.Category;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GuideDTO {

    private Integer id;

    private String firstname;

    private String lastname;

    private String email;

    private String phone;

    private int yearsOfExperience;

    @JsonIgnore()
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Trip> trips;

    public GuideDTO(Guide guide) {
        this.id = guide.getId();
        this.firstname = guide.getFirstname();
        this.lastname = guide.getLastname();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
        this.trips = guide.getTrips();
    }
}
