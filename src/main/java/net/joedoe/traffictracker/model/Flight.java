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
    @Column
    private String icao;
    @Column
    private LocalDateTime date;
    @Column
    private int altitude;
    @Column
    private int speed;
    @Column
    private String departureAirport;
    @Column
    private String departureAirportName;
    @Column
    private String airline;
    @Column
    private String airlineName;
    @SuppressWarnings("JpaDataSourceORMInspection")
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "day_id")
    private Day day;
}