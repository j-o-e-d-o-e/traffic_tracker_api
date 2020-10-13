package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.mapper.WeekMapper;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/planes/week")
public class WeekController {
    private final DayService service;
    private final WeekMapper mapper;

    public WeekController(DayService service, WeekMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping()
    public WeekDto getCurrentWeek() {
        LocalDate date = LocalDate.now().with(DayOfWeek.MONDAY);
        return mapper.toResource(service.getWeek(date));
    }

    @GetMapping("/{date}")
    public WeekDto getWeekByDate(@PathVariable("date")
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            return null;
        }
        date = date.with(DayOfWeek.MONDAY);
        return mapper.toResource(service.getWeek(date));
    }
}
