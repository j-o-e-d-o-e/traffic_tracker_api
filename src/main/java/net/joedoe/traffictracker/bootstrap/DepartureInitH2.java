package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.DepartureClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Profile("h2")
@Order(5)
@Slf4j
@Component
public class DepartureInitH2 implements CommandLineRunner {
    private final DepartureClient client;

    public DepartureInitH2(DepartureClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        client.getDepartures();
    }
}
