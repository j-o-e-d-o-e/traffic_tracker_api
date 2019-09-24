package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class WeekDto extends ResourceSupport {
    private LocalDate start_date;
    private LocalDate end_date;
    private int total;
    private int avg_planes;
    private int planes_23;
    private int planes_0;
    private int avg_altitude;
    private int avg_speed;
    private int[] weekdays;
}
