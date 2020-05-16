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
public class WeekDto extends ResourceSupport {
    private LocalDate start_date;
    private LocalDate end_date;
    private LocalDateTime now;
    private int year;
    private int month;
    private boolean prev;
    private boolean next;
    private int total;
    private int[] avg_planes;
    private int planes_23;
    private int planes_0;
    private int avg_altitude;
    private int avg_speed;
    private int[] weekdays;
}
