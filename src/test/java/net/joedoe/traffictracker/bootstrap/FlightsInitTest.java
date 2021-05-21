package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FlightsInitTest {

    public static List<Flight> createFlights() {
        Day day = new Day();
        day.setDate(LocalDate.now());
        return createFlights(day);
    }

    public static List<Flight> createFlights(Day day) {
        List<Flight> list = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            list.add(createFlight(day, LocalDateTime.of(day.getDate(), LocalTime.now().minusMinutes(i * 5))));
        return list;
    }

    public static Flight createFlight(Day day) {
        return createFlight(day, LocalDateTime.of(day.getDate(), LocalTime.now()));
    }

    public static Flight createFlight(Day day, LocalDateTime dateTime) {
        Flight flight = new Flight();
        flight.setId((long) dateTime.getHour() + dateTime.getMinute());
        flight.setDateTime(dateTime);
        flight.setAltitude((int) (Math.random() * 1500) + 600);
        flight.setSpeed((int) (Math.random() * 600) + 300);
        flight.setCallsign("Callsign");
        Plane plane = new Plane("3c56f0");
        flight.setPlane(plane);
        Airline airline = new Airline("AIR", "Airline");
        flight.setAirline(airline);
        Airport airport = new Airport("AIRP", "Airport");
        flight.setDeparture(airport);
        flight.setDay(day);
        return flight;
    }
}
