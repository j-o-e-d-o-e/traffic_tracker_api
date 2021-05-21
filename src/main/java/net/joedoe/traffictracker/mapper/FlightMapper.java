package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.model.Airline;
import net.joedoe.traffictracker.model.Airport;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.Plane;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class FlightMapper {

    public static FlightDto toDto(Flight flight) {
        if (flight == null) return null;
        LocalDateTime dateTime = flight.getDateTime();
        String icao_24 = null, departureIcao = null, departureName = null, airlineIcao = null, airlineName = null;
        Plane plane = flight.getPlane();
        if (plane != null) {
            icao_24 = plane.getIcao();
        }
        Airline airline = flight.getAirline();
        if (airline != null) {
            airlineIcao = airline.getIcao();
            airlineName = airline.getName();
        }
        Airport departure = flight.getDeparture();
        if (departure != null) {
            departureIcao = departure.getIcao();
            departureName = departure.getName();
        }
        return new FlightDto(flight.getId(), flight.getCallsign(), icao_24, dateTime, LocalDate.from(dateTime),
                flight.getAltitude(), flight.getSpeed(), departureIcao, departureName, airlineIcao, airlineName);
    }
}
