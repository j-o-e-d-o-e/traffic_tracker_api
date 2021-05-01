package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.DepartureClient;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static net.joedoe.traffictracker.client.DepartureClient.Departure;

@Order(5)
@Slf4j
@Component
public class DepartureInit implements CommandLineRunner {
    private final DepartureClient client;
    private final DayService service;

    public DepartureInit(DepartureClient client, DayService service) {
        this.client = client;
        this.service = service;
    }

    @Override
    public void run(String... args) {
//        updateDepartures(LocalDate.of(2020, 12, 1));
    }

    /**
     * only if api-call failed for departures/airports infos
     *
     * @param date only for last seven days
     */
    private void updateDepartures(LocalDate date) {
        long end = Timestamp.valueOf(LocalDateTime.of(date.plusDays(1), LocalTime.MIDNIGHT)).getTime() / 1000;
        List<Departure> departures = client.fetchDepartures(end);
        log.info("departures fetched");
        if (departures == null) return;
        for (Departure departure : departures)
            log.info(departure.toString());
        client.updateFlights(departures, date);
        service.setDepartures(date);
    }
}
