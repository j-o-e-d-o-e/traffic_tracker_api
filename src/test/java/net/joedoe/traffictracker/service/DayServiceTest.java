package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.model.Wind;
import net.joedoe.traffictracker.repo.DayRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DayServiceTest {
    @InjectMocks
    private DayService service;
    @Mock
    private DayRepository repository;
    private LocalDate date = LocalDate.now();
    private LocalDateTime dateTime = LocalDateTime.now();
    private List<Day> days = DaysInitTest.createDays(date);

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
    public void addPlane() {
        Plane plane = new Plane();
        plane.setDate(dateTime);

        when(repository.getDayByDate(date)).thenReturn(Optional.of(new Day()));
        service.addPlane(plane);

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
    public void getDayById() {
        Day day = days.get(0);

        when(repository.getDayById(anyLong())).thenReturn(Optional.of(day));
        Day dayByDate = service.getDayById(day.getId());

        assertEquals(date, dayByDate.getDate());
    }

    @Test
    public void getDay() {
        Day day = days.get(0);

        when(repository.getDayByDate(date)).thenReturn(Optional.of(day));
        Day dayByDate = service.getDay(date);

        assertEquals(date, dayByDate.getDate());
    }

    @Test
    public void getWeek() {
        LocalDate date = this.date.with(DayOfWeek.MONDAY);

        when(repository.getDaysByDateBetween(date.minusDays(1), date.plusWeeks(1))).thenReturn(Optional.of(days));
        List<Day> daysOfWeek = service.getWeek(date);

        assertEquals(days.size(), daysOfWeek.size());
    }

    @Test
    public void getMonth() {
        LocalDate date = this.date.withDayOfMonth(1);

        when(repository.getDaysByDateBetween(date.minusDays(1), date.plusMonths(1))).thenReturn(Optional.of(days));
        List<Day> daysOfMonth = service.getMonth(date);

        assertEquals(days.size(), daysOfMonth.size());
    }

    @Test
    public void getYear() {
        LocalDate date = this.date.withDayOfMonth(1).withMonth(1);

        when(repository.getDaysByDateBetween(date.minusDays(1), date.plusYears(1))).thenReturn(Optional.of(days));
        List<Day> daysOfYear = service.getYear(date);

        assertEquals(days.size(), daysOfYear.size());
    }
}
