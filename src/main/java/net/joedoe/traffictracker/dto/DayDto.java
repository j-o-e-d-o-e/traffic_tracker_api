package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayDto {
    private Long id;
    private LocalDate date;
    private LocalDateTime now;
    private String weekday;
    private boolean prev;
    private boolean next;
    private int total;
    private Integer[] avg_flights;
    private int avg_altitude;
    private int avg_speed;
    private boolean less_than_thirty_flights;
    private float wind_speed;
    private int[] hours_flight;
    private Integer[] hours_wind;
    private DeparturesDto departures;
    private List<MapEntryDto> airports;
    private boolean flights;
}
