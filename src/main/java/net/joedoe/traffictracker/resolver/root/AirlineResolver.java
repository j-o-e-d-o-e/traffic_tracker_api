package net.joedoe.traffictracker.resolver.root;

import jakarta.validation.Valid;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Airline;
import net.joedoe.traffictracker.service.AirlineService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class AirlineResolver {
    private final AirlineService service;

    public AirlineResolver(AirlineService service) {
        this.service = service;
    }

    @QueryMapping
    public Airline airline(@Argument String icao) {
        return service.findByIcao(icao.toUpperCase());
    }

    @QueryMapping
    public PageDto<Airline> airlines(@Argument @Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return service.findAll(req);
    }
}
