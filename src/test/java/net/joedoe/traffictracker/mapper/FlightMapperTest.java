package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.bootstrap.FlightsInitTest;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Flight;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FlightMapperTest {
    private static Flight flight;

    @BeforeAll
    public static void setUp() {
        Day day = DaysInitTest.createDay(LocalDate.now());
        flight = FlightsInitTest.createFlight(day);
    }

    @Test
    public void ToDto() {
        FlightDto flightDto = FlightMapper.toDto(flight);

        assertEquals(flight.getPlane().getIcao(), flightDto.getIcao_24());
        assertEquals(flight.getDateTime(), flightDto.getDate_time());
        assertEquals(flight.getAltitude(), flightDto.getAltitude());
        assertEquals(flight.getSpeed(), flightDto.getSpeed());
    }
}