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
    private DeparturesDto departures;
    private List<MapEntryDto> airports;
    private StatsPlane plane_with_most_flights;
    private StatsPlane plane_with_most_flights_within_one_day;
    private PlaneDto max_altitude;
    private PlaneDto min_altitude;
    private PlaneDto max_speed;
    private PlaneDto min_speed;
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
