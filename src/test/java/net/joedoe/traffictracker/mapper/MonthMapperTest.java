package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class MonthMapperTest {
    private final MonthMapper mapper = new MonthMapper();
    private final LocalDate startDate = LocalDate.now().withDayOfMonth(1);
    private final List<Day> days = DaysInit.createDays(LocalDate.now().getDayOfMonth() - 1);

    @Test
    public void toResource() {
        MonthDto monthDto = mapper.toResource(days);

        assertEquals("/planes/month/" + startDate.getYear() + "/" + startDate.getMonthValue(), monthDto.getLink("self").getHref());
        LocalDate tmp = startDate.minusMonths(1);
        assertEquals("/planes/month/" + tmp.getYear() + "/" + tmp.getMonthValue(), monthDto.getLink("prev_month").getHref());
        assertFalse(monthDto.hasLink("next_month"));
        assertEquals("/planes/week/" + startDate, monthDto.getLink("weeks").getHref());
        assertEquals("/planes/year/" + startDate.getYear(), monthDto.getLink("year").getHref());
    }

    @Test
    public void daysToMonthDTO() {
        MonthDto monthDto = mapper.toResource(days);

        assertEquals(startDate, monthDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, monthDto.getTotal());
        assertEquals(days.stream().mapToInt(Day::getPlanes23).sum(), monthDto.getPlanes_23());
        assertEquals(days.stream().mapToInt(Day::getPlanes0).sum(), monthDto.getPlanes_0());
        assertEquals(days.stream().mapToInt(Day::getAbsAltitude).sum() / total,
                monthDto.getAvg_altitude());
        assertEquals(days.stream().mapToInt(Day::getAbsSpeed).sum() / total,
                monthDto.getAvg_speed());
        assertEquals((days.stream().filter(Day::isLessThanThirtyPlanes).count()) / (float) days.size() * 100,
                monthDto.getDays_with_less_than_thirty_planes(), 0.01f);
        int[] monthDays = new int[startDate.getMonth().length(startDate.isLeapYear())];
        for (Day day : days) {
            monthDays[day.getDate().getDayOfMonth() - 1] = day.getTotal();
        }
//        for (int i = 0; i < monthDays.length; i++) {
//            log.info(i + ": " + monthDays[i] + " " + monthDto.getDays()[i]);
//        }
        assertArrayEquals(monthDays, monthDto.getDays());
        Integer[] avgPlanes = new Integer[LocalDate.now().getDayOfMonth()];
        Arrays.fill(avgPlanes, total / days.size());
        assertArrayEquals(avgPlanes, monthDto.getAvg_planes());
    }
}