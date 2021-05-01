package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.bootstrap.FlightsInit;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Flight;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class FlightMapperTest {
    private Flight flight;

    @Before
    public void setUp() {
        Day day = DaysInit.createDay(LocalDate.now());
        flight = FlightsInit.createFlight(day);
    }

    @Test
    public void ToDto() {
        FlightDto flightDto = FlightMapper.toDto(flight);

        assertEquals(flight.getIcao(), flightDto.getIcao_24());
        assertEquals(flight.getDate(), flightDto.getDate_time());
        assertEquals(flight.getAltitude(), flightDto.getAltitude());
        assertEquals(flight.getSpeed(), flightDto.getSpeed());
    }
}