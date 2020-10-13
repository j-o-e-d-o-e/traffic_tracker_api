package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@Slf4j
public class YearMapperTest {
    private final YearMapper mapper = new YearMapper();
    private final LocalDate startDate = LocalDate.now().withDayOfMonth(1).withMonth(1);
    private final List<Day> days = DaysInit.createDays(LocalDate.now().getDayOfYear() - 1);

    @Test
    public void toResource() {
        YearDto yearDto = mapper.toResource(days);

        assertEquals("/planes/year/" + startDate.getYear(), yearDto.getLink("self").getHref());
        assertEquals("/planes/year/" + (startDate.getYear() - 1), yearDto.getLink("prev_year").getHref());
        assertFalse(yearDto.hasLink("next_year"));
        assertEquals("/planes/month/" + startDate.getYear() + "/" + startDate.getMonthValue(), yearDto.getLink("months").getHref());

    }

    @Test
    public void daysToYearDTO() {
        YearDto yearDto = mapper.toResource(days);

        assertEquals(startDate, yearDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, yearDto.getTotal());
        assertEquals(days.stream().mapToInt(Day::getPlanes23).sum(), yearDto.getPlanes_23());
        assertEquals(days.stream().mapToInt(Day::getPlanes0).sum(), yearDto.getPlanes_0());
        assertEquals(days.stream().mapToInt(Day::getAbsAltitude).sum() / total,
                yearDto.getAvg_altitude());
        assertEquals(days.stream().mapToInt(Day::getAbsSpeed).sum() / total,
                yearDto.getAvg_speed());
        assertEquals((days.stream().filter(Day::isLessThanThirtyPlanes).count()) / (float) days.size() * 100,
                yearDto.getDays_with_less_than_thirty_planes(), 0.01f);
        int[] months = new int[12];
        for (Day day : days) {
            months[day.getDate().getMonth().getValue() - 1] += day.getTotal();
        }
//        for (int i = 0; i < months.length; i++) {
//            log.info(i + ": " + months[i] + " " + yearDto.getMonths()[i]);
//        }
        assertArrayEquals(months, yearDto.getMonths());

        Integer[] avgPlanes = new Integer[LocalDate.now().getMonthValue()];
        int avgPlanesVal = (int) (total / Arrays.stream(months).filter(m -> m != 0).count());
        Arrays.fill(avgPlanes, avgPlanesVal);
        assertArrayEquals(avgPlanes, yearDto.getAvg_planes());
    }
}