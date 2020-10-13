package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.DepartureAirportClient;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static net.joedoe.traffictracker.client.DepartureAirportClient.*;

@Profile("h2")
@Order(5)
@Slf4j
@Component
public class DepartureInitH2 implements CommandLineRunner {
    private final DepartureAirportClient client;

    public DepartureInitH2(DepartureAirportClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        client.getDepartures();
    }
}
