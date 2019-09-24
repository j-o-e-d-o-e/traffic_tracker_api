package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Slf4j
public class WeekMapperTest {
    private WeekMapper mapper = new WeekMapper();
    private LocalDate date = LocalDate.now().with(DayOfWeek.MONDAY);
    private List<Day> days;

    @Before
    public void setUp() {
        days = DaysInitTest.createDays(date)
                .stream().filter(d -> {
                    LocalDate date = d.getDate();
                    return (date.isEqual(this.date) || date.isAfter(this.date)) && date.isBefore(this.date.plusWeeks(1));
                })
                .collect(Collectors.toList());
    }

    @Test
    public void toResource() {
        WeekDto weekDto = mapper.toResource(days);

        assertEquals(date, weekDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, weekDto.getTotal());
        assertEquals(total / days.size(), weekDto.getAvg_planes());
        assertEquals(days.stream().mapToInt(Day::getPlanes23).sum(), weekDto.getPlanes_23());
        assertEquals(days.stream().mapToInt(Day::getPlanes0).sum(), weekDto.getPlanes_0());
        assertEquals(days.stream().mapToInt(Day::getAbsAltitude).sum() / total,
                weekDto.getAvg_altitude());
        assertEquals(days.stream().mapToInt(Day::getAbsSpeed).sum() / total,
                weekDto.getAvg_speed());
        int[] weekdays = new int[7];
        for (int i = 0; i < days.size(); i++) {
            weekdays[i] = days.get(i).getTotal();
        }
        for (int i = 0; i < weekdays.length; i++) {
            log.info(i + ": " + weekdays[i] + " " + weekDto.getWeekdays()[i]);
        }
        assertArrayEquals(weekdays, weekDto.getWeekdays());
    }
}