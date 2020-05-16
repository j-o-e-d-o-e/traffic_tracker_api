package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.ml.Estimator;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.ForecastRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@Order(3)
public class ForecastInit implements CommandLineRunner {
    private ForecastRepository forecastRepository;
    private Estimator estimator;

    public ForecastInit(ForecastRepository forecastRepository, Estimator estimator) {
        this.forecastRepository = forecastRepository;
        this.estimator = estimator;
    }

    @Override
    public void run(String... args) {
        if (forecastRepository.findAll().size() == 0) {
            estimator.predict();
        }
    }
}
