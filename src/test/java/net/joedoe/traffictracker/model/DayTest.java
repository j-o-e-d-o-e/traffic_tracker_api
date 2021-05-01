package net.joedoe.traffictracker.model;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.FlightsInit;
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
    public void addFlight() {
        Flight flight = FlightsInit.createFlight(day);

        day.addFlight(flight);

        assertEquals(1, day.getTotal());
        assertTrue(day.isLessThanThirtyFlights());
        assertEquals(flight.getDate().getHour() == 23 ? 1 : 0, day.getFlights23());
        assertEquals(flight.getDate().toLocalTime().isBefore(LocalTime.of(5, 45)) ? 1 : 0, day.getFlights0());
        assertEquals(flight.getAltitude(), day.getAvgAltitude());
        assertEquals(flight.getSpeed(), day.getAvgSpeed());
        int[] hoursFlight = new int[24];
        hoursFlight[flight.getDate().getHour()] = 1;
        assertArrayEquals(hoursFlight, day.getHoursFlight());
        assertEquals(1, day.getFlights().size());
        assertEquals(flight.getAltitude(), day.getAbsAltitude());
        assertEquals(flight.getSpeed(), day.getAbsSpeed());
    }

    @Test
    public void addFlightAfter23() {
        Flight flight = FlightsInit.createFlight(day, LocalDateTime.now().withHour(23));

        day.addFlight(flight);

        assertEquals(1, day.getFlights23());
    }

    @Test
    public void addFlightAfter0() {
        Flight flight = FlightsInit.createFlight(day, LocalDateTime.now().withHour(1));

        day.addFlight(flight);

        assertEquals(1, day.getFlights0());
    }

    @Test
    public void addFlights() {
        System.out.println(day);
        List<Flight> flights = FlightsInit.createFlights();

        for (Flight flight : flights)
            day.addFlight(flight);
        System.out.println(day);

        assertEquals(flights.size(), day.getTotal());
        assertFalse(day.isLessThanThirtyFlights());

        assertEquals(flights.stream().filter(p -> p.getDate().toLocalTime().isAfter(LocalTime.of(22, 57))).count(), day.getFlights23());
        assertEquals(flights.stream().filter(p -> p.getDate().toLocalTime().isBefore(LocalTime.of(5, 45))).count(), day.getFlights0());
        assertEquals(flights.stream().mapToInt(Flight::getAltitude).sum() / flights.size(), day.getAvgAltitude());
        assertEquals(flights.stream().mapToInt(Flight::getSpeed).sum() / flights.size(), day.getAvgSpeed());
        int[] hoursFlight = new int[24];
        for (Flight flight : flights)
            hoursFlight[flight.getDate().getHour()] += 1;
        for (int i = 0; i < 24; i++)
            log.info(i + ": " + hoursFlight[i] + " " + day.getHoursFlight()[i]);
        assertArrayEquals(hoursFlight, day.getHoursFlight());
        assertEquals(flights.size(), day.getFlights().size());
        assertEquals(flights.stream().mapToInt(Flight::getAltitude).sum(), day.getAbsAltitude());
        assertEquals(flights.stream().mapToInt(Flight::getSpeed).sum(), day.getAbsSpeed());
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
