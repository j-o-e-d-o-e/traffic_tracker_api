package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {
    private int total;
    private StatsDay day_with_most_flights;
    private StatsDay day_with_most_flights_within_one_hour;
    private float days_with_less_than_thirty_flights;
    private float hours_with_no_flights;
    private DeparturesDto departures;
    private List<MapEntryDto> airports;
    private StatsPlane plane_with_most_flights;
    private StatsPlane plane_with_most_flights_within_one_day;
    private FlightDto max_altitude;
    private FlightDto min_altitude;
    private FlightDto max_speed;
    private FlightDto min_speed;
    private List<MapEntryDto> airlines;
    private ForecastScoreDto score;

    @EqualsAndHashCode(callSuper = false)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatsDay {
        private DayDto day;
        private int stats;
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatsPlane extends StatsDay {
        private String icao;
        private int stats;
    }
}
