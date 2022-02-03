package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.bootstrap.FlightsInitTest;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.dto.WindDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.repo.DayRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DayServiceTest {
    @InjectMocks
    private DayService service;
    @Mock
    private DayRepository repository;
    @Mock
    private PlaneService planeService;
    @Mock
    private AirlineService airlineService;
    private final LocalDate date = LocalDate.now();
    private final LocalDateTime dateTime = LocalDateTime.now();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addFlight() {
        Flight flight = FlightsInitTest.createFlight(new Day(date));
        flight.setDateTime(dateTime);

        when(planeService.findOrCreate(flight.getPlane().getIcao())).thenReturn(flight.getPlane());
        when(airlineService.findOrCreate(flight.getCallsign().substring(0, 3))).thenReturn(flight.getAirline());
        when(repository.findByDateJoinFetchFlights(date)).thenReturn(Optional.of(new Day()));
        service.addFlight(flight.getPlane().getIcao(), flight);

        verify(repository, times(1)).findByDateJoinFetchFlights(any());
        verify(repository, times(1)).save(any());
    }

    @Test
    public void addWind() {
        WindDto windDto = new WindDto();
        windDto.setDateTime(dateTime);

        when(repository.findByDate(date)).thenReturn(Optional.of(new Day()));
        service.addWind(windDto);

        verify(repository, times(1)).findByDate(any());
        verify(repository, times(1)).save(any());
    }

    @Test
    public void getDay() {
        Day day = DaysInitTest.createDay(LocalDate.now().minusDays(1));

        when(repository.findByDate(date)).thenReturn(Optional.of(day));
        DayDto dayByDate = service.getDayByDate(date);

        assertEquals(date.minusDays(1), dayByDate.getDate());
    }
}
