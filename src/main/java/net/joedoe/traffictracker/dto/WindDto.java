package net.joedoe.traffictracker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WindDto {
    private LocalDateTime dateTime;
    private int deg;
    private float speed;
}
