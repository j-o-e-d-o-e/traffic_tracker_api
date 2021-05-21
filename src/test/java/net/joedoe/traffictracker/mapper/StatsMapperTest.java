package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Slf4j
public class StatsMapperTest {

    @Test
    public void iterateDays() {
        List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfYear() - 1);
        int total = days.stream().mapToInt(Day::getTotal).sum();
        int dayWithMostFlights = Collections.max(days, Comparator.comparing(Day::getTotal)).getTotal();
        StatsDto act = StatsMapper.toStatsDto(days, null);

        assertEquals(total, act.getTotal());
        assertEquals(dayWithMostFlights, act.getDay_with_most_flights().getStats());
//        log.info("" + act.getAirlines());
//        log.info(String.valueOf(act.getDays_with_less_than_thirty_flights()));
//        log.info(String.valueOf(act.getHours_with_no_flights()));
    }
}
