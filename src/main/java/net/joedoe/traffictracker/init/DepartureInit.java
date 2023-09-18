package net.joedoe.traffictracker.init;

import net.joedoe.traffictracker.service.DayService;
import org.springframework.boot.CommandLineRunner;

//@Component
//@Order(1)
public class DepartureInit implements CommandLineRunner {
    private final DayService service;

    public DepartureInit(DayService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        service.fetchDepartures();
    }
}
