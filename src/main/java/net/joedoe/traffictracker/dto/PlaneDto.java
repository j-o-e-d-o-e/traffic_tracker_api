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
public class PlaneDto extends ResourceSupport {
    private String icao_24;
    private LocalDateTime date_time;
    private LocalDate date;
    private int altitude;
    private int speed;
}
