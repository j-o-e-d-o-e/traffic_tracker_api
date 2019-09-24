package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.mapper.DayMapper;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/planes/day")
public class PlaneDayController {
    private DayService service;
    private DayMapper mapper;

    public PlaneDayController(DayService service, DayMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping()
    public DayDto getCurrentDay() {
        return mapper.toResource(service.getDay(LocalDate.now()));
    }

    @GetMapping("/{date}")
    public DayDto getDayByDate(@PathVariable("date")
                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            return null;
        }
        return mapper.toResource(service.getDay(date));
    }

    @GetMapping("/id/{id}")
    public DayDto getDayById(@PathVariable("id") Long id) {
        if (id == null) {
            return null;
        }
        return mapper.toResource(service.getDayById(id));
    }
}
