package net.joedoe.traffictracker.resolver.field;

import jakarta.validation.Valid;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class DayFieldResolver{
    private final FlightService flightService;

    public DayFieldResolver(FlightService flightService) {
        this.flightService = flightService;
    }

    @SchemaMapping
    public PageDto<Flight> flights(Day day, @Argument @Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return flightService.findByDate(day.getDate(), req);
    }
}
