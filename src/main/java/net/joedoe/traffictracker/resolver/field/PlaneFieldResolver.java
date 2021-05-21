package net.joedoe.traffictracker.resolver.field;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Slf4j
@Component
@Validated
public class PlaneFieldResolver implements GraphQLResolver<Plane> {
    private final FlightService flightService;

    public PlaneFieldResolver(FlightService flightService) {
        this.flightService = flightService;
    }

    public PageDto<Flight> flights(Plane plane, @Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return flightService.findByPlaneIcao(plane.getIcao(), req);
    }
}
