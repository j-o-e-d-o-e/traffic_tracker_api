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
public class MonthDto {
    private LocalDate start_date;
    private LocalDate end_date;
    private LocalDateTime now;
    private int year;
    private int month;
    private LocalDate first_day_of_month;
    private boolean prev;
    private boolean next;
    private int total;
    private Integer[] avg_flights;
    private int flights_23;
    private int flights_0;
    private int avg_altitude;
    private int avg_speed;
    private float days_with_less_than_thirty_flights;
    private int[] days;
    private DeparturesDto departures;
    private List<MapEntryDto> airports;
}
