package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class YearDto extends ResourceSupport {
    private LocalDate start_date;
    private LocalDate end_date;
    private LocalDateTime now;
    private int total;
    private int avg_planes;
    private int planes_23;
    private int planes_0;
    private int avg_altitude;
    private int avg_speed;
    private float days_with_less_than_thirty_planes;
    private int[] months;
}
