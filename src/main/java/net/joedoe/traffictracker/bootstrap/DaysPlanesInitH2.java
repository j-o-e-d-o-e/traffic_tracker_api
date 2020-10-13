package net.joedoe.traffictracker.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Plane;
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
public class DaysPlanesInitH2 implements CommandLineRunner {
    private DayRepository repository;

    public DaysPlanesInitH2(DayRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        Day day = repository.getDayByDate(LocalDate.now()).orElse(null);
        if (day != null) {
            loadPlanes(day, LocalDateTime.now());
            loadWinds(day, LocalDateTime.now());
            repository.save(day);
        }
        int[] days = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 30, 31, 32, 365, 366, 367};
        for (int i : days) {
            day = new Day();
            day.setDate(LocalDate.now().minusDays(i));
            loadPlanes(day, LocalDateTime.now().minusDays(i));
            loadWinds(day, LocalDateTime.now().minusDays(i));
            repository.save(day);
        }
    }

    private void loadPlanes(Day day, LocalDateTime dateTime) {
        Plane plane1 = new Plane();
        plane1.setAltitude(991);
        plane1.setDate(dateTime.minus(9, ChronoUnit.MINUTES));
        plane1.setIcao("3c56f0");
        plane1.setSpeed(343);
        plane1.setDepartureAirport("AIRP");
        plane1.setDepartureAirportName("Airport");
        plane1.setAirline("AIR");
        plane1.setAirlineName("Airline");
        day.addPlane(plane1);

        Plane plane2 = new Plane();
        plane2.setAltitude(914);
        plane2.setDate(dateTime.minusMinutes(7));
        plane2.setIcao("3c6743");
        plane2.setSpeed(355);
        plane2.setDepartureAirport("AIR2");
        plane2.setDepartureAirportName("Airport2");
        plane2.setAirline("AIS");
        plane2.setAirlineName("Aisline");
        day.addPlane(plane2);

        Plane plane3 = new Plane();
        plane3.setAltitude(1097);
        plane3.setDate(dateTime.minusSeconds(300));
        plane3.setIcao("40083b");
        plane3.setSpeed(362);
        plane3.setDepartureAirport("AIR3");
        plane3.setDepartureAirportName("Airport3");
        plane3.setAirline("AIT");
        plane3.setAirlineName("AITline");
        day.addPlane(plane3);
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
