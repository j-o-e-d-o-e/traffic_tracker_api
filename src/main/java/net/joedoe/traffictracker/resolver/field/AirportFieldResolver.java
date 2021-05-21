package net.joedoe.traffictracker.resolver.field;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Airport;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Slf4j
@Component
@Validated
public class AirportFieldResolver implements GraphQLResolver<Airport> {
    private final FlightService flightService;

    public AirportFieldResolver(FlightService flightService) {
        this.flightService = flightService;
    }

    public PageDto<Flight> flights(Airport airport, @Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return flightService.findByAirportIcao(airport.getIcao().toUpperCase(), req);
    }
}
