package net.joedoe.traffictracker.resolver.root;

import jakarta.validation.Valid;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.PlaneService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class PlaneResolver {
    private final PlaneService service;

    public PlaneResolver(PlaneService service) {
        this.service = service;
    }

    @QueryMapping
    public Plane plane(@Argument String icao) {
        return service.findByIcao(icao.toUpperCase());
    }

    @QueryMapping
    public PageDto<Plane> planes(@Argument @Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return service.findAll(req);
    }
}
