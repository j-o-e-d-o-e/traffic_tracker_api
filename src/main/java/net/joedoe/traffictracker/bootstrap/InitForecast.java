package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.ForecastClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(4)
public class InitForecast implements CommandLineRunner {
    private final ForecastClient client;

    public InitForecast(ForecastClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        client.predict();
        log.info("Forecast predicted");
    }
}
