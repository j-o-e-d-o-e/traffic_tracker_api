package net.joedoe.traffictracker.resolver.root;

import jakarta.validation.Valid;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Controller
@Validated
public class DayResolver {
    private final DayService service;

    public DayResolver(DayService service) {
        this.service = service;
    }

    @QueryMapping
    public Day day(@Argument LocalDate date) {
        if (date == null) return service.findDayLatest();
        return service.findDayByDate(date);
    }

    @QueryMapping
    public PageDto<Day> days(@Argument @Valid PageRequestDto req) {
        if (req == null) req = new PageRequestDto(0, 10);
        return service.findAll(req);
    }
}
