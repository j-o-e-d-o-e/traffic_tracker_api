package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.DepartureAirportClient;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static net.joedoe.traffictracker.client.DepartureAirportClient.PlaneDepart;

@Order(5)
@Slf4j
//@Component
public class DepartureInit implements CommandLineRunner {
    private final DepartureAirportClient client;
    private final DayService service;

    public DepartureInit(DepartureAirportClient client, DayService service) {
        this.client = client;
        this.service = service;
    }

    @Override
    public void run(String... args) {
//        updateDepartures(LocalDate.of(2020, 10, 7));
    }

    /**
     * only if api-call failed for departures/airports infos
     * @param date only for last seven days
     */
    private void updateDepartures(LocalDate date) {
        long end = Timestamp.valueOf(LocalDateTime.of(date.plusDays(1), LocalTime.MIDNIGHT)).getTime() / 1000;
        List<PlaneDepart> planes = client.fetchDepartures(end);
        if (planes == null) return;
        client.updatePlanes(planes, date);
        service.setDepartures(date);
    }
}
