package net.joedoe.traffictracker.model;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.FlightsInitTest;
import net.joedoe.traffictracker.bootstrap.WindsInitTest;
import net.joedoe.traffictracker.dto.WindDto;
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
        System.out.println(day);
        List<Flight> flights = FlightsInitTest.createFlights();

        for (Flight flight : flights)
            day.addFlight(flight);
        System.out.println(day);

        assertEquals(flights.size(), day.getTotal());
        assertFalse(day.isLessThanThirtyFlights());

        assertEquals(flights.stream().filter(f -> f.getDateTime().toLocalTime().isAfter(LocalTime.of(22, 57))).count(), day.getFlights23());
        assertEquals(flights.stream().filter(f -> f.getDateTime().toLocalTime().isBefore(LocalTime.of(5, 45))).count(), day.getFlights0());
        assertEquals(flights.stream().mapToInt(Flight::getAltitude).sum() / flights.size(), day.getAvgAltitude());
        assertEquals(flights.stream().mapToInt(Flight::getSpeed).sum() / flights.size(), day.getAvgSpeed());
        int[] hoursFlight = new int[24];
        for (Flight flight : flights)
            hoursFlight[flight.getDateTime().getHour()] += 1;
        for (int i = 0; i < 24; i++)
            log.info(i + ": " + hoursFlight[i] + " " + day.getHoursFlight()[i]);
        assertArrayEquals(hoursFlight, day.getHoursFlight());
        assertEquals(flights.size(), day.getFlights().size());
        assertEquals(flights.stream().mapToInt(Flight::getAltitude).sum(), day.getAbsAltitude());
        assertEquals(flights.stream().mapToInt(Flight::getSpeed).sum(), day.getAbsSpeed());
    }

    @Test
    public void addWinds() {
        List<WindDto> windDtos = WindsInitTest.createWinds(date);

        for (WindDto windDto : windDtos)
            day.addWind(windDto);

        assertEquals(windDtos.stream().mapToDouble(WindDto::getSpeed).sum() / windDtos.size(),
                day.getWindSpeed(), 0.01f);
        assertEquals(windDtos.size(), day.getAbsWind());
        int[] hoursWind = new int[24];
        for (WindDto windDto : windDtos) {
            hoursWind[windDto.getDateTime().getHour()] = windDto.getDeg();
        }
        assertArrayEquals(hoursWind, day.getHoursWind());
        assertEquals(windDtos.stream().mapToDouble(WindDto::getSpeed).sum(),
                day.getAbsWindSpeed(), 0.01f);
    }

    @Test
    public void addWind() {
        WindDto windDto = WindsInitTest.createWind(date, 0);

        day.addWind(windDto);

        assertEquals(windDto.getSpeed(), day.getWindSpeed(), 0.01f);
        assertEquals(1, day.getAbsWind());
        int[] hoursWind = new int[24];
        hoursWind[windDto.getDateTime().getHour()] = windDto.getDeg();
        assertArrayEquals(hoursWind, day.getHoursWind());
        assertEquals(windDto.getSpeed(), day.getAbsWindSpeed(), 0.01f);
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
