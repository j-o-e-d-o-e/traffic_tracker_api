package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.mapper.StatsMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.ForecastScore;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<Day> days = dayService.findAllJoinFetchFlights();
        if (days == null) return null;
        ForecastScore forecastScore = forecastService.find();
        if (forecastScore == null) return null;
        return StatsMapper.toStatsDto(days, forecastScore);
    }
}
