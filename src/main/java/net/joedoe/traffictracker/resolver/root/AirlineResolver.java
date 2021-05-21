package net.joedoe.traffictracker.resolver.root;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Airline;
import net.joedoe.traffictracker.service.AirlineService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Slf4j
@Component
@Validated
public class AirlineResolver implements GraphQLQueryResolver {
    private final AirlineService service;

    public AirlineResolver(AirlineService service) {
        this.service = service;
    }

    public Airline airline(String icao) {
        return service.findByIcao(icao.toUpperCase());
    }

    @SuppressWarnings("unused")
    public PageDto<Airline> airlines(@Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return service.findAll(req);
    }
}
