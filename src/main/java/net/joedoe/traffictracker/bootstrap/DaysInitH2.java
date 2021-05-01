package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.Wind;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Profile("h2")
@Order(2)
@Slf4j
@Component
public class DaysInitH2 implements CommandLineRunner {
    private final DayRepository repository;

    public DaysInitH2(DayRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        Day day = repository.getDayByDate(LocalDate.now()).orElse(null);
        if (day != null) {
            loadFlights(day, LocalDateTime.now());
            loadWinds(day, LocalDateTime.now());
            repository.save(day);
        }
        int[] days = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 30, 31, 32, 365, 366, 367};
        for (int i : days) {
            day = new Day();
            day.setDate(LocalDate.now().minusDays(i));
            loadFlights(day, LocalDateTime.now().minusDays(i));
            loadWinds(day, LocalDateTime.now().minusDays(i));
            repository.save(day);
        }
    }

    private void loadFlights(Day day, LocalDateTime dateTime) {
        Flight flight1 = new Flight();
        flight1.setAltitude(991);
        flight1.setDate(dateTime.minus(9, ChronoUnit.MINUTES));
        flight1.setIcao("3c56f0");
        flight1.setSpeed(343);
        flight1.setDepartureAirport("AIRP");
        flight1.setDepartureAirportName("Airport");
        flight1.setAirline("AIR");
        flight1.setAirlineName("Airline");
        day.addFlight(flight1);

        Flight flight2 = new Flight();
        flight2.setAltitude(914);
        flight2.setDate(dateTime.minusMinutes(7));
        flight2.setIcao("3c6743");
        flight2.setSpeed(355);
        flight2.setDepartureAirport("AIR2");
        flight2.setDepartureAirportName("Airport2");
        flight2.setAirline("AIS");
        flight2.setAirlineName("Aisline");
        day.addFlight(flight2);

        Flight flight3 = new Flight();
        flight3.setAltitude(1097);
        flight3.setDate(dateTime.minusSeconds(300));
        flight3.setIcao("40083b");
        flight3.setSpeed(362);
        flight3.setDepartureAirport("AIR3");
        flight3.setDepartureAirportName("Airport3");
        flight3.setAirline("AIT");
        flight3.setAirlineName("AITline");
        day.addFlight(flight3);
    }

    private void loadWinds(Day day, LocalDateTime dateTime) {
        Wind wind1 = new Wind();
        wind1.setDate(dateTime);
        wind1.setDeg(280);
        wind1.setSpeed(12f);
        day.addWind(wind1);

        Wind wind2 = new Wind();
        wind2.setDate(dateTime.minusHours(1));
        wind2.setDeg(320);
        wind2.setSpeed(12f);
        day.addWind(wind2);

        Wind wind3 = new Wind();
        wind3.setDate(dateTime.plusHours(1));
        wind3.setDeg(10);
        wind3.setSpeed(12f);
        day.addWind(wind3);

        Wind wind4 = new Wind();
        wind4.setDate(dateTime.plusHours(2));
        wind4.setDeg(30);
        wind4.setSpeed(12f);
        day.addWind(wind4);
    }
}
