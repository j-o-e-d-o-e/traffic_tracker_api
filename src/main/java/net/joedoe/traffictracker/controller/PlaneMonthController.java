package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.mapper.MonthMapper;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/planes/month")
public class PlaneMonthController {
    private DayService service;
    private MonthMapper mapper;

    public PlaneMonthController(DayService service, MonthMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping()
    public MonthDto getCurrentMonth() {
        return mapper.toResource(service.getMonth(LocalDate.now().withDayOfMonth(1)));
    }

    @GetMapping("/{date}")
    public MonthDto getMonthByDate(@PathVariable("date")
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            return null;
        }
        return mapper.toResource(service.getMonth(date.withDayOfMonth(1)));
    }
}
