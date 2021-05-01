package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.WindClient;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@Order(1)
public class WindInit implements CommandLineRunner {
    private final DayRepository repository;
    private final WindClient client;

    public WindInit(DayRepository repository, WindClient client) {
        this.repository = repository;
        this.client = client;
    }

    @Override
    public void run(String... args) {
        if (!repository.getDayByDate(LocalDate.now()).isPresent())
            repository.save(new Day(LocalDate.now()));
        LocalTime now = LocalTime.now();
        if (now.getHour() >= 6 && now.getMinute() > 30)
            client.fetchWeather();
    }
}
