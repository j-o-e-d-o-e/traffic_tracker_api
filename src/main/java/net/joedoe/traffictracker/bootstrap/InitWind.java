package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.WindClient;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Slf4j
//@Component
@Order(2)
public class InitWind implements CommandLineRunner {
    private final WindClient client;

    public InitWind(WindClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        LocalTime now = LocalTime.now();
        if (now.getHour() >= 6 && now.getMinute() > 30)
            client.fetchWeather();
        log.info("Wind fetched");
    }
}
