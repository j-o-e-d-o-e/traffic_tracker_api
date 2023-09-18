package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeekServiceTest {
    @InjectMocks
    private WeekService service;
    @Mock
    private DayRepository repository;

    @Test
    public void getWeek() {
        LocalDate date = LocalDate.now().with(DayOfWeek.MONDAY);
        List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfWeek().getValue() - 1);

        when(repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusWeeks(1))).thenReturn(Optional.of(days));
        WeekDto week = service.getWeekByDate(date);

        assertEquals(date, week.getStart_date());
    }
}
