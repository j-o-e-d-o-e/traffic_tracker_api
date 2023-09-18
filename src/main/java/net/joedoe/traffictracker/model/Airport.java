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
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String icao;
    private String name;
    @Enumerated(value = EnumType.STRING)
    private Region region = Region.UNKNOWN;
    @ToString.Exclude
    @OneToMany(mappedBy = "departure", fetch = FetchType.LAZY)
    private List<Flight> flights = new ArrayList<>();

    public Airport(String icao) {
        this.icao = icao;
        if (icao.startsWith("ED")) this.region = Region.NATIONAL;
        else if (icao.startsWith("E") || icao.startsWith("L")) this.region = Region.INTERNATIONAL;
        else this.region = Region.INTERCONTINENTAL;
    }

    public Airport(String icao, String name) {
        this(icao);
        this.name = name;
    }

    @SuppressWarnings("unused")
    public void addFlight(Flight flight) {
        flights.add(flight);
        flight.setDeparture(this);
    }
}
