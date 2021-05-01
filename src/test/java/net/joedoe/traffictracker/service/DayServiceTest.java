package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.Wind;
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
    private final LocalDate date = LocalDate.now();
    private final LocalDateTime dateTime = LocalDateTime.now();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addNewDay() {
        service.addNewDay();
        verify(repository, times(1)).save(any());
    }

    @Test
    public void addFlight() {
        Flight flight = new Flight();
        flight.setDate(dateTime);

        when(repository.getDayByDate(date)).thenReturn(Optional.of(new Day()));
        service.addFlight(flight);

        verify(repository, times(1)).getDayByDate(any());
        verify(repository, times(1)).save(any());
    }

    @Test
    public void addWind() {
        Wind wind = new Wind();
        wind.setDate(dateTime);

        when(repository.getDayByDate(date)).thenReturn(Optional.of(new Day()));
        service.addWind(wind);

        verify(repository, times(1)).getDayByDate(any());
        verify(repository, times(1)).save(any());
    }

    @Test
    public void getDay() {
        Day day = DaysInit.createDay(LocalDate.now().minusDays(1));

        when(repository.getDayByDate(date)).thenReturn(Optional.of(day));
        DayDto dayByDate = service.getDayByDate(date);

        assertEquals(date.minusDays(1), dayByDate.getDate());
    }
}
