package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayDto extends ResourceSupport {
    private LocalDate date;
    private LocalDateTime now;
    private String weekday;
    private boolean prev;
    private boolean next;
    private int total;
    private Integer[] avg_planes;
    private int avg_altitude;
    private int avg_speed;
    private boolean less_than_thirty_planes;
    private float wind_speed;
    private int[] hours_plane;
    private Integer[] hours_wind;
    private DeparturesDto departures;
    private List<MapEntryDto> airports;
}
