package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.mapper.StatsMapper;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.ForecastScoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/planes/stats")
public class StatsController {
    private final DayService dayService;
    private final ForecastScoreService forecastService;
    private final StatsMapper mapper;

    public StatsController(DayService dayService, ForecastScoreService forecastService, StatsMapper mapper) {
        this.dayService = dayService;
        this.forecastService = forecastService;
        this.mapper = mapper;
    }

    @GetMapping()
    public StatsDto getStats() {
        return mapper.toStatsDto(dayService.findAll(), forecastService.find());
    }
}
