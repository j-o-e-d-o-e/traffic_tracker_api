package net.joedoe.traffictracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class Stats {
    private int daysTotal;
    private int flightsTotal;
    private StatsDay dayWithMostFlights = new StatsDay();
    private StatsDay dayWithMostFlightsWithinOneHour = new StatsDay();
    private int daysWithLessThanThirtyFlightsAbs;
    private float daysWithLessThanThirtyFlights;
    private int hoursWithNoFlightsAbs;
    private float hoursWithNoFlights;

    private int departuresContinental;
    private int departuresContinentalAbs;
    private int departuresInternational;
    private int departuresInternationalAbs;
    private int departuresNational;
    private int departuresNationalAbs;
    private int departuresUnknown;
    private int departuresUnknownAbs;
    private Map<String, Integer> airports = new HashMap<>();

    private StatsPlane flightWithMaxAltitude = new StatsPlane();
    private StatsPlane flightWithMinAltitude = new StatsPlane();
    private StatsPlane flightWithMaxSpeed = new StatsPlane();
    private StatsPlane flightWithMinSpeed = new StatsPlane();

    private Map<String, Integer> airlines = new HashMap<>();

    private Map<String, Integer> planes = new HashMap<>();
    private StatsPlane planeWithMostFlights = new StatsPlane();
    private StatsPlane planeWithMostFlightsWithinOneDay = new StatsPlane();

    @EqualsAndHashCode
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatsDay implements Comparable<StatsDay>{
        private LocalDate date;
        private int stats;

        @Override
        public int compareTo(@NotNull StatsDay o) {
            return Integer.compare(stats, o.stats);
        }
    }

    @EqualsAndHashCode
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatsPlane implements Comparable<StatsPlane> {
        private LocalDate date;
        private String icao;
        private int stats;

        @Override
        public int compareTo(@NotNull StatsPlane o) {
            return Integer.compare(stats, o.stats);
        }
    }
}
