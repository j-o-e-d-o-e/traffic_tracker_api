package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.YearDto;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class YearServiceTest {
    @InjectMocks
    private YearService service;
    @Mock
    private DayRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getYear() {
        LocalDate date = LocalDate.now().withDayOfMonth(1).withMonth(1);
        List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfYear() - 1);

        when(repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusYears(1))).thenReturn(Optional.of(days));
        YearDto year = service.getYear(date);

        assertEquals(date, year.getStart_date());
    }
}