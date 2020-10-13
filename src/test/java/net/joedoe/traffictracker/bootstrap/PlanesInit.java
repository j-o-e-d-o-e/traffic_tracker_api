package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Plane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PlanesInit {

    public static List<Plane> createPlanes() {
        Day day = new Day();
        day.setDate(LocalDate.now());
        return createPlanes(day);
    }

    public static List<Plane> createPlanes(Day day) {
        List<Plane> list = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            list.add(createPlane(day, LocalDateTime.of(day.getDate(), LocalTime.now().minusMinutes(i * 5))));
        return list;
    }

    public static Plane createPlane(Day day) {
        return createPlane(day, LocalDateTime.of(day.getDate(), LocalTime.now()));
    }

    public static Plane createPlane(Day day, LocalDateTime dateTime) {
        Plane plane = new Plane();
        plane.setId((long) dateTime.getHour() + dateTime.getMinute());
        plane.setIcao("3c56f0");
        plane.setDate(dateTime);
        plane.setAltitude((int) (Math.random() * 1500) + 600);
        plane.setSpeed((int) (Math.random() * 600) + 300);
        plane.setDepartureAirport("AIRP");
        plane.setDepartureAirportName("Airport");
        plane.setAirline("AIR");
        plane.setAirlineName("Airline");
        plane.setDay(day);
        return plane;
    }
}
