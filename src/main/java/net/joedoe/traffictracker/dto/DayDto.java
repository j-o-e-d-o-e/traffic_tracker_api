package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class DayDto extends ResourceSupport {
    private LocalDate date;
    private String weekday;
    private int total;
    private int avg_planes;
    private int avg_altitude;
    private int avg_speed;
    private float wind_speed;
    private int[] hours_plane;
    private int[] hours_wind;
}
