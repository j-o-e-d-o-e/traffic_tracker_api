package net.joedoe.traffictracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime date_time;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate date;
    private int altitude;
    private int speed;
    private String departure_icao;
    private String departure_name;
    private String airline_icao;
    private String airline_name;
    private boolean photo;
}
