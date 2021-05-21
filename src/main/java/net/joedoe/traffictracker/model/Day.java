package net.joedoe.traffictracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.joedoe.traffictracker.dto.WindDto;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@PropertySource({"classpath:departure.properties"})
@Entity
@NoArgsConstructor
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private int total;
    private boolean lessThanThirtyFlights = true;
    private int flights23;
    private int flights0;
    private int avgAltitude;
    private int avgSpeed;
    @SuppressWarnings("JpaAttributeTypeInspection")
    private int[] hoursFlight = new int[24];
    @ToString.Exclude
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "day", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Flight> flights = new ArrayList<>();
    private float windSpeed;
    @SuppressWarnings("JpaAttributeTypeInspection")
    private int[] hoursWind = new int[24];
    private int absAltitude;
    private int absSpeed;
    private int absWind;
    private float absWindSpeed;
    private Float departuresContinental;
    private Float departuresInternational;
    private Float departuresNational;
    private Float departuresUnknown;
    private Integer departuresContinentalAbs;
    private Integer departuresInternationalAbs;
    private Integer departuresNationalAbs;
    private Integer departuresUnknownAbs;
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Integer> departuresTop = new HashMap<>();

    public Day(LocalDate date) {
        this.date = date;
    }

    public void addFlight(Flight flight) {
        total += 1;
        lessThanThirtyFlights = total < 30;
        if (flight.getDateTime().toLocalTime().isAfter(LocalTime.of(22, 57))) {
            flights23 += 1;
        } else if (flight.getDateTime().toLocalTime().isBefore(LocalTime.of(5, 45))) {
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
        hoursFlight[flight.getDateTime().getHour()] += 1;
        flight.setDay(this);
        flights.add(flight);
    }

    public void addWind(WindDto windDto) {
        absWind += 1;
        absWindSpeed += windDto.getSpeed();
        windSpeed = Math.round(absWindSpeed / absWind * 100) / 100f;
        hoursWind[windDto.getDateTime().getHour()] = windDto.getDeg();
    }

    public void setDepartures() {
        departuresContinentalAbs = 0;
        departuresInternationalAbs = 0;
        departuresNationalAbs = 0;
        departuresUnknownAbs = 0;
        Map<String, Integer> airportsOccurrences = new HashMap<>();
        for (Flight flight : flights) {
            Airport departure = flight.getDeparture();
            if (departure == null || departure.getIcao() == null || departure.getRegion() == null) {
                departuresUnknownAbs++;
                continue;
            }
            switch (departure.getRegion()) {
                case INTERCONTINENTAL:
                    departuresContinentalAbs++;
                    break;
                case INTERNATIONAL:
                    departuresInternationalAbs++;
                    break;
                case NATIONAL:
                    departuresNationalAbs++;
                    break;
                case UNKNOWN:
                    departuresUnknownAbs++;
                    break;
            }
            String departureName = departure.getName() != null ? departure.getName() : departure.getIcao();
            if (airportsOccurrences.containsKey(departureName))
                airportsOccurrences.put(departureName, airportsOccurrences.get(departureName) + 1);
            else
                airportsOccurrences.put(departureName, 1);

        }
        if (total == 0 || (departuresContinentalAbs == 0 && departuresInternationalAbs == 0 &&
                departuresNationalAbs == 0 && departuresUnknownAbs == 0)) return;
        departuresContinental = Math.round(departuresContinentalAbs / (float) total * 100) / 100f;
        departuresInternational = Math.round(departuresInternationalAbs / (float) total * 100) / 100f;
        departuresNational = Math.round(departuresNationalAbs / (float) total * 100) / 100f;
        departuresUnknown = 1 - departuresContinental - departuresInternational - departuresNational;

        this.departuresTop = airportsOccurrences.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void clearFlights() {
        this.flights.clear();
    }
}
