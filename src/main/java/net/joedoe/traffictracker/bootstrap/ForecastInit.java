package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.ForecastClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(3)
public class ForecastInit implements CommandLineRunner {
    private final ForecastClient client;

    public ForecastInit(ForecastClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        client.predict();
        client.score();
    }
}
