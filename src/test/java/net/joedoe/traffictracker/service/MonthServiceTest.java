package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MonthServiceTest {
    @InjectMocks
    private MonthService service;
    @Mock
    private DayRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMonth() {
        LocalDate date = LocalDate.now().withDayOfMonth(1);
        List<Day> days = DaysInit.createDays(LocalDate.now().getDayOfMonth() - 1);

        when(repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusMonths(1))).thenReturn(Optional.of(days));
        MonthDto month = service.getMonth(date);

        assertEquals(date, month.getStart_date());
    }
}
