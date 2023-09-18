package net.joedoe.traffictracker.model;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.FlightsInitTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
public class DayTest {
    private static Day day;

    @BeforeEach
    public void setUp() {
        day = new Day();
        day.setDate(LocalDate.now());
    }

    @Test
    public void addFlight() {
        Flight flight = FlightsInitTest.createFlight(day);

        day.addFlight(flight);

        assertEquals(1, day.getTotal());
        assertTrue(day.isLessThanThirtyFlights());
        assertEquals(flight.getDateTime().getHour() == 23 ? 1 : 0, day.getFlights23());
        assertEquals(flight.getDateTime().toLocalTime().isBefore(LocalTime.of(5, 45)) ? 1 : 0, day.getFlights0());
        assertEquals(flight.getAltitude(), day.getAvgAltitude());
        assertEquals(flight.getSpeed(), day.getAvgSpeed());
        int[] hoursFlight = new int[24];
        hoursFlight[flight.getDateTime().getHour()] = 1;
        assertArrayEquals(hoursFlight, day.getHoursFlight());
        assertEquals(1, day.getFlights().size());
        assertEquals(flight.getAltitude(), day.getAbsAltitude());
        assertEquals(flight.getSpeed(), day.getAbsSpeed());
    }

    @Test
    public void addFlightAfter23() {
        Flight flight = FlightsInitTest.createFlight(day, LocalDateTime.now().withHour(23));

        day.addFlight(flight);

        assertEquals(1, day.getFlights23());
    }

    @Test
    public void addFlightAfter0() {
        Flight flight = FlightsInitTest.createFlight(day, LocalDateTime.now().withHour(1));

        day.addFlight(flight);

        assertEquals(1, day.getFlights0());
    }

    @Test
    public void addFlights() {
        List<Flight> flights = FlightsInitTest.createFlights();

        for (Flight flight : flights) day.addFlight(flight);

        assertEquals(flights.size(), day.getTotal());
        assertFalse(day.isLessThanThirtyFlights());
        assertEquals(flights.stream().filter(f -> f.getDateTime().toLocalTime().isAfter(LocalTime.of(22, 57))).count(), day.getFlights23());
        assertEquals(flights.stream().filter(f -> f.getDateTime().toLocalTime().isBefore(LocalTime.of(5, 45))).count(), day.getFlights0());
        assertEquals(flights.stream().mapToInt(Flight::getAltitude).sum() / flights.size(), day.getAvgAltitude());
        assertEquals(flights.stream().mapToInt(Flight::getSpeed).sum() / flights.size(), day.getAvgSpeed());
        int[] hoursFlight = new int[24];
        for (Flight flight : flights) hoursFlight[flight.getDateTime().getHour()] += 1;
        assertArrayEquals(hoursFlight, day.getHoursFlight());
        assertEquals(flights.size(), day.getFlights().size());
        assertEquals(flights.stream().mapToInt(Flight::getAltitude).sum(), day.getAbsAltitude());
        assertEquals(flights.stream().mapToInt(Flight::getSpeed).sum(), day.getAbsSpeed());
    }

    @Test
    public void setDepartures() {
        List<Flight> flights = FlightsInitTest.createFlights();
        day.setFlights(flights);

        day.setTotal(flights.size());
        day.setDepartures();

        Integer expContinent = Math.toIntExact(flights.stream().filter(f -> f.getDeparture().getRegion() == Region.INTERCONTINENTAL).count());
        assertEquals(expContinent, day.getDeparturesContinentalAbs());
        Integer expInter = Math.toIntExact(flights.stream().filter(f -> f.getDeparture().getRegion() == Region.INTERNATIONAL).count());
        assertEquals(expInter, day.getDeparturesInternationalAbs());
        Integer expNation = Math.toIntExact(flights.stream().filter(f -> f.getDeparture().getRegion() == Region.NATIONAL).count());
        assertEquals(expNation, day.getDeparturesInternationalAbs());
        assertEquals(0, day.getDeparturesUnknown(), 0.01);
        String airportName = flights.get(0).getDeparture().getName();
        assertTrue(day.getDeparturesTop().containsKey(airportName));
        int actVal = day.getDeparturesTop().get(airportName);
        assertEquals(flights.size(), actVal);

    }
}
