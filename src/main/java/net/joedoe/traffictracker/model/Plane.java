package net.joedoe.traffictracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Plane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String icao;
    @ToString.Exclude
    @OneToMany(mappedBy = "plane", fetch = FetchType.LAZY)
    private List<Flight> flights = new ArrayList<>();

    public Plane(String icao) {
        this.icao = icao;
    }

    @SuppressWarnings("unused")
    public void addFlight(Flight flight) {
        flights.add(flight);
        flight.setPlane(this);
    }
}
