package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonthServiceTest {
    @InjectMocks
    private MonthService service;
    @Mock
    private DayRepository repository;

    @Test
    public void getMonth() {
        LocalDate date = LocalDate.now().withDayOfMonth(1);
        List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfMonth() - 1);

        when(repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusMonths(1))).thenReturn(Optional.of(days));
        MonthDto month = service.getMonthByDate(date);

        assertEquals(date, month.getStart_date());
    }
}
