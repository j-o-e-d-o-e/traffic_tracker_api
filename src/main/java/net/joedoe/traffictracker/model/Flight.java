package net.joedoe.traffictracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String callsign;
    private LocalDateTime dateTime;
    private int altitude;
    private int speed;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Day day;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private Airport departure;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private Plane plane;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private Airline airline;
    @ToString.Exclude
//    @Lob
    private Byte[] photo;
}
