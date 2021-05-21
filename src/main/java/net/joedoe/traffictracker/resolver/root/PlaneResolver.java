package net.joedoe.traffictracker.resolver.root;

import graphql.kickstart.tools.GraphQLQueryResolver;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.PlaneService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Component
@Validated
public class PlaneResolver implements GraphQLQueryResolver {
    private final PlaneService service;

    public PlaneResolver(PlaneService service) {
        this.service = service;
    }

    public Plane plane(String icao) {
        return service.findByIcao(icao);
    }

    public PageDto<Plane> planes(@Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return service.findAll(req);
    }
}
