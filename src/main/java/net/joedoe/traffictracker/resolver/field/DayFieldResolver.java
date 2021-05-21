package net.joedoe.traffictracker.resolver.field;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@SuppressWarnings("unused")
@Slf4j
@Component
@Validated
public class DayFieldResolver implements GraphQLResolver<Day> {
    private final FlightService flightService;

    public DayFieldResolver(FlightService flightService) {
        this.flightService = flightService;
    }

    public PageDto<Flight> flights(Day day, @Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return flightService.findByDate(day.getDate(), req);
    }
}
