package net.joedoe.traffictracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@PropertySource({"classpath:departureAirport.properties"})
@Entity
@NoArgsConstructor
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private LocalDate date;
    @Column
    private int total;
    @Column
    private boolean lessThanThirtyFlights = true;
    @Column
    private int flights23;
    @Column
    private int flights0;
    @Column
    private int avgAltitude;
    @Column
    private int avgSpeed;
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column
    private int[] hoursFlight = new int[24];
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "day", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Flight> flights = new ArrayList<>();
    @Column
    private float windSpeed;
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column
    private int[] hoursWind = new int[24];
    @Column
    private int absAltitude;
    @Column
    private int absSpeed;
    @Column
    private int absWind;
    @Column
    private float absWindSpeed;
    @Column
    private Float departuresContinental;
    @Column
    private Float departuresInternational;
    @Column
    private Float departuresNational;
    @Column
    private Float departuresUnknown;
    @Column
    private Integer departuresContinentalAbs;
    @Column
    private Integer departuresInternationalAbs;
    @Column
    private Integer departuresNationalAbs;
    @Column
    private Integer departuresUnknownAbs;
    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Integer> departuresTop = new HashMap<>();

    public Day(LocalDate date) {
        this.date = date;
    }

    public void addFlight(Flight flight) {
        total += 1;
        lessThanThirtyFlights = total < 30;
        if (flight.getDate().toLocalTime().isAfter(LocalTime.of(22, 57))) {
            flights23 += 1;
        } else if (flight.getDate().toLocalTime().isBefore(LocalTime.of(5, 45))) {
            flights0 += 1;
        }
        int altitude = flight.getAltitude();
        if (altitude != 0) {
            absAltitude += altitude;
            avgAltitude = absAltitude / total;
        }
        int speed = flight.getSpeed();
        if (speed != 0) {
            absSpeed += speed;
            avgSpeed = absSpeed / total;
        }
        hoursFlight[flight.getDate().getHour()] += 1;
        flight.setDay(this);
        flights.add(flight);
    }

    public void addWind(Wind wind) {
        absWind += 1;
        absWindSpeed += wind.getSpeed();
        windSpeed = Math.round(absWindSpeed / absWind * 100) / 100f;
        hoursWind[wind.getDate().getHour()] = wind.getDeg();
    }

    public void clearFlights() {
        this.flights.clear();
    }
}
