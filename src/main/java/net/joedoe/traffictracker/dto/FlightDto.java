package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto {
    private Long id;
    private String callsign;
    private String icao_24;
    private LocalDateTime date_time;
    private LocalDate date;
    private int altitude;
    private int speed;
    private String departure_icao;
    private String departure_name;
    private String airline_icao;
    private String airline_name;
}
