package net.joedoe.traffictracker.init;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalTime;

@Slf4j
//@Component
//@Order(2)
public class WindInit implements CommandLineRunner {
    private final DayService service;

    public WindInit(DayService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        LocalTime now = LocalTime.now();
        if (now.getHour() >= 6 && now.getMinute() > 30) service.fetchWind();
        log.info("Wind fetched");
    }
}
