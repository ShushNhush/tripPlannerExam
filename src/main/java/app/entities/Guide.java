package app.entities;

import app.dtos.GuideDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstname;

    private String lastname;

    private String email;

    private String phone;

    private int yearsOfExperience;

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "guide")
    private Set<Trip> trips;

    public Guide(GuideDTO guideDTO) {
        this.id = guideDTO.getId();
        this.firstname = guideDTO.getFirstname();
        this.lastname = guideDTO.getLastname();
        this.email = guideDTO.getEmail();
        this.phone = guideDTO.getPhone();
        this.yearsOfExperience = guideDTO.getYearsOfExperience();
        this.trips = guideDTO.getTrips();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guide guide = (Guide) o;
        return Objects.equals(id, guide.id) && Objects.equals(phone, guide.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone);
    }
}
