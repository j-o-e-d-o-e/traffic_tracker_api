package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.dto.ForecastDayDto;
import net.joedoe.traffictracker.dto.ForecastScoreDto;
import net.joedoe.traffictracker.mapper.ForecastMapper;
import net.joedoe.traffictracker.mapper.ForecastScoreMapper;
import net.joedoe.traffictracker.service.ForecastService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/forecasts")
public class ForecastController {
    private final ForecastService service;

    public ForecastController(ForecastService service) {
        this.service = service;
    }

    @GetMapping()
    public List<ForecastDayDto> getAll() {
        return ForecastMapper.toResources(service.getForecasts());
    }

    @GetMapping("/score")
    public ForecastScoreDto getScore() {
        return ForecastScoreMapper.toResource(service.getScore());
    }
}
