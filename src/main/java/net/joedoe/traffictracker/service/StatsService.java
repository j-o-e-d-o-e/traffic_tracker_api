package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.mapper.StatsMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatsService {
    private final DayService dayService;
    private final ForecastScoreService forecastService;

    public StatsService(DayService dayService, ForecastScoreService forecastService) {
        this.dayService = dayService;
        this.forecastService = forecastService;
    }

    public StatsDto getStats() {
        return StatsMapper.toStatsDto(dayService.findAll(), forecastService.find());
    }
}
