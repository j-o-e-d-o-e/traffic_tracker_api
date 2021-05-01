package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.mapper.StatsMapper;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.ForecastScoreService;
import net.joedoe.traffictracker.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping()
    public StatsDto getStats() {
        return service.getStats();
    }
}
