package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.model.Flight;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FlightMapper {

    public static FlightDto toDto(Flight flight) {
        LocalDate date = LocalDate.from(flight.getDate());
        return new FlightDto(flight.getId(), flight.getIcao(), flight.getDate(), date, flight.getAltitude(), flight.getSpeed(),
                flight.getDepartureAirport(), flight.getDepartureAirportName(), flight.getAirline(), flight.getAirlineName());
    }
}
