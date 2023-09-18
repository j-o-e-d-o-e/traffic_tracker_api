package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.FlightsInitTest;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.repo.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {
    @InjectMocks
    private FlightService service;
    @Mock
    private FlightRepository repository;
    private final List<Flight> flights = FlightsInitTest.createFlights();

    @Test
    public void getFlightsByDate() {
        Page<Flight> exp = new PageImpl<>(flights, PageRequest.of(0, 20), 1);

        when(repository.getFlightsByDateTimeBetweenOrderByDateTimeDesc(any(), any(), any())).thenReturn(Optional.of(exp));
        Page<FlightDto> act = service.getFlightsByDate(LocalDate.now(), Pageable.unpaged());

        assertEquals(exp.getSize(), act.getSize());
    }

    @Test
    public void getFlightsByIcao() {
        Page<Flight> exp = new PageImpl<>(flights, PageRequest.of(0, 20), 1);

        when(repository.findByPlaneIcao(any(), any())).thenReturn(Optional.of(exp));
        Page<FlightDto> act = service.getByPlaneIcao("", null);

        assertEquals(exp.getSize(), act.getSize());
    }
}