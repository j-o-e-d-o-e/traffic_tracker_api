package net.joedoe.traffictracker.resolver.root;

import jakarta.validation.Valid;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Airport;
import net.joedoe.traffictracker.model.Region;
import net.joedoe.traffictracker.service.AirportService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class AirportResolver {
    private final AirportService service;

    public AirportResolver(AirportService service) {
        this.service = service;
    }

    @QueryMapping
    public Airport departure(@Argument String icao) {
        return service.findByIcao(icao.toUpperCase());
    }

    public PageDto<Airport> departures(@Argument @Valid PageRequestDto req, @Argument Region region) {
        if (req == null) req = new PageRequestDto(0, 10);
        if (region == null) return service.findAll(req);
        else return service.findAllByRegion(region, req);
    }
}
