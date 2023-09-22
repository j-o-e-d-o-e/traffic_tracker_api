package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class WeekMapperTest {
    private final LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
    private final List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfWeek().getValue() - 1);

    @Test
    public void daysToWeekDto() {
        WeekDto weekDto = WeekMapper.toDto(startDate, days, true, true);

        assertEquals(startDate, weekDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, weekDto.getTotal());
        assertEquals(days.stream().mapToInt(Day::getFlights23).sum(), weekDto.getFlights_23());
        assertEquals(days.stream().mapToInt(Day::getFlights0).sum(), weekDto.getFlights_0());
        assertEquals(days.stream().mapToInt(Day::getAbsAltitude).sum() / total,
                weekDto.getAvg_altitude());
        assertEquals(days.stream().mapToInt(Day::getAbsSpeed).sum() / total,
                weekDto.getAvg_speed());
        Integer[] weekdays = new Integer[7];
        for (int i = 0; i < days.size(); i++) weekdays[i] = days.get(i).getTotal();

        assertArrayEquals(weekdays, weekDto.getWeekdays());
    }
}