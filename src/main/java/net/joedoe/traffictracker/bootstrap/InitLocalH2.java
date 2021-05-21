package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.WindDto;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Profile("h2")
@Order(1)
@Slf4j
@Component
public class InitLocalH2 implements CommandLineRunner {
    private final DayService dayService;
    private final FlightService flightService;
    private final Random rand = new Random();

    public InitLocalH2(DayService dayService, FlightService flightService) {
        this.dayService = dayService;
        this.flightService = flightService;
    }

    @Override
    public void run(String... args) {
        dayService.addDay();
        // WindClient
        LocalDateTime time = LocalDateTime.now();
        for (int i = 0; i < 24 - time.getHour(); i++) {
            WindDto windDto = new WindDto();
            windDto.setDateTime(time.plusHours(i));
            windDto.setDeg(rand.nextInt(365));
            windDto.setSpeed(rand.nextInt(20) + rand.nextFloat());
            dayService.addWind(windDto);
        }
        // FlightClient
        List<String> p_icao = Arrays.asList("4bccba", "3c6743", "40083b");
        final List<String> a_icao = Arrays.asList("GWI", "EWG", "DLH");
        for (int i = 0; i < 3; i++) {
            String callsign = a_icao.get(i) + UUID.randomUUID().toString().substring(0, 4);
            Flight newFlight = new Flight();
            newFlight.setCallsign(callsign);
            newFlight.setDateTime(LocalDateTime.now());
            newFlight.setAltitude(rand.nextInt(100) + 900);
            newFlight.setSpeed(rand.nextInt(100) + 300);
            dayService.addFlight(p_icao.get(i), newFlight);
        }
        // DepartureClient
        List<String> d_icao = Arrays.asList("EDDB", "EDDH", "LTFJ");
        LocalDate date = time.toLocalDate();
        List<Flight> flights = flightService.getByDate(date);
        for (int i = 0; i < 3; i++) {
            flightService.setDeparture(d_icao.get(i), flights.get(i));
        }
        dayService.setDepartures(date);
        log.info("Local init done");
    }
}
