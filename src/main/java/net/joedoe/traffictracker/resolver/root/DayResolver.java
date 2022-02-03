package net.joedoe.traffictracker.resolver.root;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDate;

@Slf4j
@Component
@Validated
public class DayResolver implements GraphQLQueryResolver {
    private final DayService service;

    public DayResolver(DayService service) {
        this.service = service;
    }

    public Day day(LocalDate date) {
        if (date == null) date = LocalDate.now().minusDays(1);
        return service.findDayByDate(date);
    }

    public PageDto<Day> days(@Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return service.findAll(req);
    }
}
