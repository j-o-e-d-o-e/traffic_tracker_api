package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.ml.model.ForecastDaily;
import net.joedoe.traffictracker.service.ForecastService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/planes/forecast")
public class ForecastController {
    private ForecastService service;

    public ForecastController(ForecastService service) {
        this.service = service;
    }

    @GetMapping()
    public List<ForecastDaily> findAll() {
        return service.findAll();
    }
}
