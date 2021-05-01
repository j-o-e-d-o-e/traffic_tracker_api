package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class WeekServiceTest {
    @InjectMocks
    private WeekService service;
    @Mock
    private DayRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getWeek() {
        LocalDate date = LocalDate.now().with(DayOfWeek.MONDAY);
        List<Day> days = DaysInit.createDays(LocalDate.now().getDayOfWeek().getValue() - 1);

        when(repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusWeeks(1))).thenReturn(Optional.of(days));
        WeekDto week = service.getWeek(date);

        assertEquals(date, week.getStart_date());
    }
}
