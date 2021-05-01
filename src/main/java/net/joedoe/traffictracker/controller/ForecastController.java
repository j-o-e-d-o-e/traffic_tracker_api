package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.ForecastDayDto;
import net.joedoe.traffictracker.dto.ForecastScoreDto;
import net.joedoe.traffictracker.mapper.ForecastMapper;
import net.joedoe.traffictracker.mapper.ForecastScoreMapper;
import net.joedoe.traffictracker.service.ForecastScoreService;
import net.joedoe.traffictracker.service.ForecastService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@ApiIgnore
@RestController
@RequestMapping("/api/forecasts")
public class ForecastController {
    private final ForecastService service;
    private final ForecastScoreService scoreService;

    public ForecastController(ForecastService service, ForecastScoreService scoreService) {
        this.service = service;
        this.scoreService = scoreService;
    }

    @GetMapping()
    public List<ForecastDayDto> getAll() {
        return ForecastMapper.toResources(service.findAll());
    }

    @GetMapping("/score")
    public ForecastScoreDto getScore() {
        return ForecastScoreMapper.toResource(scoreService.find());
    }
}
