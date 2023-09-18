package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {
    private int days_total;
    private int flights_total;
    private StatsDay day_with_most_flights;
    private StatsDay day_with_most_flights_within_one_hour;
    private float days_with_less_than_thirty_flights;
    private float hours_with_no_flights;
    private DeparturesDto departures;
    private List<MapEntryDto> airports;
    private StatsPlane plane_with_most_flights;
    private StatsPlane plane_with_most_flights_within_one_day;
    private StatsPlane max_altitude;
    private StatsPlane min_altitude;
    private StatsPlane max_speed;
    private StatsPlane min_speed;
    private List<MapEntryDto> airlines;
    private ForecastScoreDto score;

    @EqualsAndHashCode
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatsDay {
        private LocalDate date;
        private int stats;
    }

    @EqualsAndHashCode
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatsPlane {
        private LocalDate date;
        private String icao;
        private int stats;
    }
}
