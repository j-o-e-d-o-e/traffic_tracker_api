package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class WeekMapperTest {
    private final WeekMapper mapper = new WeekMapper();
    private final LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
    private final List<Day> days = DaysInit.createDays(LocalDate.now().getDayOfWeek().getValue() - 1);

    @Test
    public void toResource(){
        WeekDto weekDto = mapper.toResource(days);

        assertEquals("/planes/week/" + startDate.minusWeeks(1), weekDto.getLink("prev_week").getHref());
        assertFalse(weekDto.hasLink("next_week"));
        assertEquals("/planes/day/" + startDate, weekDto.getLink("days").getHref());
        assertEquals("/planes/month/" + startDate.getYear() +"/" + startDate.getMonthValue(), weekDto.getLink("month").getHref());
    }

    @Test
    public void daysToWeekDto() {
        WeekDto weekDto = mapper.toResource(days);

        assertEquals(startDate, weekDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, weekDto.getTotal());
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
//        for (int i = 0; i < weekdays.length; i++) {
//            log.info(i + ": " + weekdays[i] + " " + weekDto.getWeekdays()[i]);
//        }
        assertArrayEquals(weekdays, weekDto.getWeekdays());
        int[] avgPlanes = new int[days.size()];
        Arrays.fill(avgPlanes, total / days.size());
        assertArrayEquals(avgPlanes, weekDto.getAvg_planes());
    }
}