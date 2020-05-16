package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.mapper.YearMapper;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/planes/year")
public class PlaneYearController {
    private DayService service;
    private YearMapper mapper;

    public PlaneYearController(DayService service, YearMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping()
    public YearDto getCurrentYear() {
        LocalDate date = LocalDate.now().withDayOfMonth(1).withMonth(1);
        return mapper.toResource(service.getYear(date));
    }

    @GetMapping("/{year}")
    public YearDto getYearByDate(@PathVariable("year") Integer year) {
        if (year == null) {
            return null;
        }
        LocalDate date = LocalDate.of(year, 1, 1);
        return mapper.toResource(service.getYear(date));
    }
}
