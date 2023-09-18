package net.joedoe.traffictracker.init;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.service.ForecastService;
import org.springframework.boot.CommandLineRunner;

@Slf4j
//@Component
//@Order(3)
@SuppressWarnings("unused")
public class ForecastInit implements CommandLineRunner {
    private final ForecastService service;

    public ForecastInit(ForecastService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        service.predict();
        log.info("Forecasts predicted");
    }
}
