package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.bootstrap.FlightsInitTest;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.ForecastScore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {
    @InjectMocks
    private StatsService service;
    @Mock
    private DayService dayService;
    @Mock
    private ForecastService forecastService;
    private final List<Day> daysJoinedFlights = DaysInitTest.createDays(10).stream().peek(d -> FlightsInitTest.createFlights(d).forEach(d::addFlight)).toList();

    @Test
    public void getStats() {
        int daysTotal = daysJoinedFlights.size();
        int flightsTotal = daysJoinedFlights.stream().mapToInt(Day::getTotal).sum();
        int hoursWithNoFlightsAbs = daysJoinedFlights.stream().mapToInt(d -> (int) Arrays.stream(d.getHoursFlight(), 6, 23)
                .filter(h -> h == 0).count()).sum();
        float hoursWithNoFlights = (hoursWithNoFlightsAbs / ((float) 17 * daysTotal)) * 100;
        hoursWithNoFlights = Math.round(hoursWithNoFlights * 100) / 100f;
        ForecastScore forecastScore = new ForecastScore();
        forecastScore.setPrecision(1f);
        forecastScore.setMeanAbsoluteError(2f);
        forecastScore.setConfusionMatrix(new int[][]{{1, 2}, {3, 4}});

        when(dayService.findAllJoinFetchFlights()).thenReturn(daysJoinedFlights);
        when(forecastService.getScore()).thenReturn(forecastScore);
        StatsDto act = service.getStats();

        assertEquals(daysTotal, act.getDays_total());
        assertEquals(flightsTotal, act.getFlights_total());
        assertEquals(hoursWithNoFlights, act.getHours_with_no_flights(), 0.1f);
    }
}