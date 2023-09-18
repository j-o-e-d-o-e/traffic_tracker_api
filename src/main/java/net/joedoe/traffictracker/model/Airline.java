package net.joedoe.traffictracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String icao;
    private String name;
    @ToString.Exclude
    @OneToMany(mappedBy = "airline", fetch = FetchType.LAZY)
    private List<Flight> flights = new ArrayList<>();

    public Airline(String icao) {
        this.icao = icao;
    }

    public Airline(String icao, String name) {
        this.icao = icao;
        this.name = name;
    }

    @SuppressWarnings("unused")
    public void addFlight(Flight flight) {
        flights.add(flight);
        flight.setAirline(this);
    }
}
