package net.joedoe.traffictracker.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Wind {
    private Long id;
    private LocalDateTime date;
    private int deg;
    private float speed;
}