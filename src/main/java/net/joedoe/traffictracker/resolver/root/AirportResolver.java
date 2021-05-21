package net.joedoe.traffictracker.resolver.root;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Airport;
import net.joedoe.traffictracker.model.Region;
import net.joedoe.traffictracker.service.AirportService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Slf4j
@Component
@Validated
public class AirportResolver implements GraphQLQueryResolver {
    private final AirportService service;

    public AirportResolver(AirportService service) {
        this.service = service;
    }

    public Airport departure(String icao) {
        return service.findByIcao(icao.toUpperCase());
    }

    public PageDto<Airport> departures(@Valid PageRequestDto req, Region region) {
        if (req == null) req = new PageRequestDto(0, 10);
        if (region == null) return service.findAll(req);
        else return service.findAllByRegion(region, req);
    }
}
