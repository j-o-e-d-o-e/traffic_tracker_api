package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@Slf4j
public class MonthMapperTest {
    private final LocalDate startDate = LocalDate.now().withDayOfMonth(1);
    private final List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfMonth() - 1);



    @Test
    public void toDto() {
        MonthDto monthDto = MonthMapper.toDto(startDate, days);

        assertEquals(startDate, monthDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, monthDto.getTotal());
        assertEquals(days.stream().mapToInt(Day::getFlights23).sum(), monthDto.getFlights_23());
        assertEquals(days.stream().mapToInt(Day::getFlights0).sum(), monthDto.getFlights_0());
        assertEquals(days.stream().mapToInt(Day::getAbsAltitude).sum() / total,
                monthDto.getAvg_altitude());
        assertEquals(days.stream().mapToInt(Day::getAbsSpeed).sum() / total,
                monthDto.getAvg_speed());
        assertEquals((days.stream().filter(Day::isLessThanThirtyFlights).count()) / (float) days.size() * 100,
                monthDto.getDays_with_less_than_thirty_flights(), 0.01f);
        int[] monthDays = new int[startDate.getMonth().length(startDate.isLeapYear())];
        for (Day day : days) {
            monthDays[day.getDate().getDayOfMonth() - 1] = day.getTotal();
        }
//        for (int i = 0; i < monthDays.length; i++) {
//            log.info(i + ": " + monthDays[i] + " " + monthDto.getDays()[i]);
//        }
        assertArrayEquals(monthDays, monthDto.getDays());
        Integer[] avgFlights = new Integer[LocalDate.now().getDayOfMonth()];
        Arrays.fill(avgFlights, total / days.size());
        assertArrayEquals(avgFlights, monthDto.getAvg_flights());
    }
}