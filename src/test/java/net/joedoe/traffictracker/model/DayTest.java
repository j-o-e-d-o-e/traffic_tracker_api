package net.joedoe.traffictracker.model;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.PlanesInit;
import net.joedoe.traffictracker.bootstrap.WindsInit;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class DayTest {
    private final LocalDate date = LocalDate.now();
    private Day day;

    @Before
    public void setUp() {
        day = new Day();
        day.setDate(LocalDate.now());
    }

    @Test
    public void addPlane() {
        Plane plane = PlanesInit.createPlane(day);

        day.addPlane(plane);

        assertEquals(1, day.getTotal());
        assertTrue(day.isLessThanThirtyPlanes());
        assertEquals(plane.getDate().getHour() == 23 ? 1 : 0, day.getPlanes23());
        assertEquals(plane.getDate().toLocalTime().isBefore(LocalTime.of(5, 45)) ? 1 : 0, day.getPlanes0());
        assertEquals(plane.getAltitude(), day.getAvgAltitude());
        assertEquals(plane.getSpeed(), day.getAvgSpeed());
        int[] hoursPlane = new int[24];
        hoursPlane[plane.getDate().getHour()] = 1;
        assertArrayEquals(hoursPlane, day.getHoursPlane());
        assertEquals(1, day.getPlanes().size());
        assertEquals(plane.getAltitude(), day.getAbsAltitude());
        assertEquals(plane.getSpeed(), day.getAbsSpeed());
    }

    @Test
    public void addPlaneAfter23() {
        Plane plane = PlanesInit.createPlane(day, LocalDateTime.now().withHour(23));

        day.addPlane(plane);

        assertEquals(1, day.getPlanes23());
    }

    @Test
    public void addPlaneAfter0() {
        Plane plane = PlanesInit.createPlane(day, LocalDateTime.now().withHour(1));

        day.addPlane(plane);

        assertEquals(1, day.getPlanes0());
    }

    @Test
    public void addPlanes() {
        System.out.println(day);
        List<Plane> planes = PlanesInit.createPlanes();

        for (Plane plane : planes)
            day.addPlane(plane);
        System.out.println(day);

        assertEquals(planes.size(), day.getTotal());
        assertFalse(day.isLessThanThirtyPlanes());

        assertEquals(planes.stream().filter(p -> p.getDate().toLocalTime().isAfter(LocalTime.of(22, 57))).count(), day.getPlanes23());
        assertEquals(planes.stream().filter(p -> p.getDate().toLocalTime().isBefore(LocalTime.of(5, 45))).count(), day.getPlanes0());
        assertEquals(planes.stream().mapToInt(Plane::getAltitude).sum() / planes.size(), day.getAvgAltitude());
        assertEquals(planes.stream().mapToInt(Plane::getSpeed).sum() / planes.size(), day.getAvgSpeed());
        int[] hoursPlane = new int[24];
        for (Plane plane : planes)
            hoursPlane[plane.getDate().getHour()] += 1;
        for (int i = 0; i < 24; i++)
            log.info(i + ": " + hoursPlane[i] + " " + day.getHoursPlane()[i]);
        assertArrayEquals(hoursPlane, day.getHoursPlane());
        assertEquals(planes.size(), day.getPlanes().size());
        assertEquals(planes.stream().mapToInt(Plane::getAltitude).sum(), day.getAbsAltitude());
        assertEquals(planes.stream().mapToInt(Plane::getSpeed).sum(), day.getAbsSpeed());
    }

    @Test
    public void addWinds() {
        List<Wind> winds = WindsInit.createWinds(date);

        for (Wind wind : winds)
            day.addWind(wind);

        assertEquals(winds.stream().mapToDouble(Wind::getSpeed).sum() / winds.size(),
                day.getWindSpeed(), 0.01f);
        assertEquals(winds.size(), day.getAbsWind());
        int[] hoursWind = new int[24];
        for (Wind wind : winds) {
            hoursWind[wind.getDate().getHour()] = wind.getDeg();
        }
        assertArrayEquals(hoursWind, day.getHoursWind());
        assertEquals(winds.stream().mapToDouble(Wind::getSpeed).sum(),
                day.getAbsWindSpeed(), 0.01f);
    }

    @Test
    public void addWind() {
        Wind wind = WindsInit.createWind(date, 0);

        day.addWind(wind);

        assertEquals(wind.getSpeed(), day.getWindSpeed(), 0.01f);
        assertEquals(1, day.getAbsWind());
        int[] hoursWind = new int[24];
        hoursWind[wind.getDate().getHour()] = wind.getDeg();
        assertArrayEquals(hoursWind, day.getHoursWind());
        assertEquals(wind.getSpeed(), day.getAbsWindSpeed(), 0.01f);
    }
}
