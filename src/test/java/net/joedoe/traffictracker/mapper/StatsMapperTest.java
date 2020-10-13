package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class StatsMapperTest {
    private StatsMapper mapper;
    private final List<Day> days = DaysInit.createDays(LocalDate.now().getDayOfYear() - 1);

    @Before
    public void setUp() {
        DayMapper dayMapper = new DayMapper();
        mapper = new StatsMapper(dayMapper, new PlaneMapper());
    }

    @Test
    public void iterate() {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        int dayWithMostFlights = Collections.max(days, Comparator.comparing(Day::getTotal)).getTotal();
        StatsDto act = mapper.toStatsDto(days, null);

        assertEquals(total, act.getTotal());
        assertEquals(dayWithMostFlights, act.getDay_with_most_flights().getStats());
        log.info("" + act.getAirlines());
    }
}