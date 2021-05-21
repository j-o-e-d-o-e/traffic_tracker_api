package net.joedoe.traffictracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.joedoe.traffictracker.util.PropertiesHandler;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    private static String prefix0;
    private static String prefix1;
    private static String prefix2;

    static {
        try {
            Properties prop = PropertiesHandler.getProperties("src/main/resources/departure.properties");
            Airport.prefix0 = prop.getProperty("prefix-0");
            Airport.prefix1 = prop.getProperty("prefix-1");
            Airport.prefix2 = prop.getProperty("prefix-2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Airport(String icao) {
        this.icao = icao;
        if (icao.startsWith(prefix0)) {
            this.region = Region.NATIONAL;
        } else if (icao.startsWith(prefix1) || icao.startsWith(prefix2)) {
            this.region = Region.INTERNATIONAL;
        } else {
            this.region = Region.INTERCONTINENTAL;
        }
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
