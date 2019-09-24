package net.joedoe.traffictracker.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
public class Wind {
    private Long id;
    private LocalDateTime date;
    private int deg;
    private float speed;
}