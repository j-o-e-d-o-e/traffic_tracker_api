package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Plane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PlanesInitTest {

    public static List<Plane> createPlanes(LocalDate date) {
        ArrayList<Plane> planes = new ArrayList<>();

        Plane plane1 = new Plane();
        plane1.setId(1L);
        plane1.setIcao("3c56f0");
        plane1.setDate(LocalDateTime.of(date, LocalTime.now().withHour(12)));
        plane1.setAltitude(991);
        plane1.setSpeed(343);
        plane1.setDay(new Day());
        planes.add(plane1);

        Plane plane2 = new Plane();
        plane2.setId(2L);
        plane2.setIcao("3c56f0");
        plane2.setDate(LocalDateTime.of(date, LocalTime.now().withHour(23)));
        plane2.setAltitude(991);
        plane2.setSpeed(343);
        plane2.setDay(new Day());
        planes.add(plane2);

        Plane plane3 = new Plane();
        plane3.setId(3L);
        plane3.setIcao("3c56f0");
        plane3.setDate(LocalDateTime.of(date, LocalTime.now().withHour(1)));
        plane3.setAltitude(991);
        plane3.setSpeed(343);
        plane3.setDay(new Day());
        planes.add(plane3);

        return planes;
    }
}
