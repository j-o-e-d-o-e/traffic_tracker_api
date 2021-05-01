package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.FlightsInit;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.mapper.FlightMapper;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.repo.FlightRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class FlightServiceTest {
    @InjectMocks
    private FlightService service;
    @Mock
    private FlightRepository repository;
    private final List<Flight> flights = FlightsInit.createFlights();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getFlightById() {
        Flight flight = flights.get(0);
        FlightDto exp = FlightMapper.toDto(flight);

        when(repository.getFlightById((flight.getId()))).thenReturn(Optional.of(flight));
        FlightDto act = service.getFlightById(flight.getId());

        assertEquals(exp.getDate(), act.getDate());
    }

    @Test
    public void getFlightsByDate() {
        Page<Flight> exp = new PageImpl<>(flights, PageRequest.of(0, 20), 1);

        when(repository.getFlightsByDateBetweenOrderByDateDesc(any(), any(), any())).thenReturn(Optional.of(exp));
        Page<FlightDto> act = service.getFlightsByDate(LocalDate.now(), Pageable.unpaged());

        assertEquals(exp.getSize(), act.getSize());
    }

    @Test
    public void getFlightsByIcao() {
        Page<Flight> exp = new PageImpl<>(flights, PageRequest.of(0, 20), 1);

        when(repository.getFlightsByIcaoOrderByDateDesc(anyString(), any())).thenReturn(Optional.of(exp));
        Page<FlightDto> act = service.getFlightsByIcao("", null);

        assertEquals(exp.getSize(), act.getSize());
    }
}