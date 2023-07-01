package ru.practicum.explorewithme.model.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    Long id;
    @Column(name = "latitude", nullable = false)
    private Double lat;
    @Column(name = "longitude", nullable = false)
    private Double lon;

    public Location() {

    }

    public Location(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id) && Objects.equals(lat, location.lat) &&
                Objects.equals(lon, location.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lat, lon);
    }
}
