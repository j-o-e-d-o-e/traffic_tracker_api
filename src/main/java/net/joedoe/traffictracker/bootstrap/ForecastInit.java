package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.ForecastClient;
import net.joedoe.traffictracker.repo.ForecastRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(3)
public class ForecastInit implements CommandLineRunner {
    private ForecastRepository forecastRepository;
    private ForecastClient client;

    public ForecastInit(ForecastRepository forecastRepository, ForecastClient client) {
        this.forecastRepository = forecastRepository;
        this.client = client;
    }

    @Override
    public void run(String... args) {
        if (forecastRepository.findAll().size() == 0) {
            client.predict();
        }
    }
}
