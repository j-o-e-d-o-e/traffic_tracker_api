package net.joedoe.traffictracker.resolver.field;

import jakarta.validation.Valid;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class PlaneFieldResolver {
    private final FlightService flightService;

    public PlaneFieldResolver(FlightService flightService) {
        this.flightService = flightService;
    }

    @SchemaMapping
    public PageDto<Flight> flights(Plane plane, @Argument @Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return flightService.findByPlaneIcao(plane.getIcao().toUpperCase(), req);
    }
}
