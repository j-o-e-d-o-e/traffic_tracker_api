package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DayServiceTest {
    @Mock
    private DayRepository repository;
    @InjectMocks
    private DayService service;
    private final LocalDate date = LocalDate.now();

    @Test
    public void getDay() {
        Day day = DaysInitTest.createDay(LocalDate.now().minusDays(1));

        when(repository.findByDate(date)).thenReturn(Optional.of(day));
        DayDto dayByDate = service.getDayByDate(date);

        assertEquals(date.minusDays(1), dayByDate.getDate());
    }
}
