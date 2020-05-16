package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@Slf4j
public class MonthMapperTest {
    private MonthMapper mapper = new MonthMapper();
    private LocalDate date = LocalDate.now().withDayOfMonth(1);
    private List<Day> days;

    @Before
    public void setUp() {
        days = DaysInitTest.createDays(date)
                .stream().filter(d -> {
                    LocalDate date = d.getDate();
                    return (date.isEqual(this.date) || date.isAfter(this.date)) && date.isBefore(this.date.plusMonths(1));
                })
                .collect(Collectors.toList());
    }

    @Test
    public void toResource() {
        MonthDto monthDto = mapper.toResource(days);
        int daysOfMonth = date.getMonth().length(date.isLeapYear());

        assertEquals(date, monthDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, monthDto.getTotal());
//        assertEquals(total / days.size(), monthDto.getAvg_planes());
        assertEquals(days.stream().mapToInt(Day::getPlanes23).sum(), monthDto.getPlanes_23());
        assertEquals(days.stream().mapToInt(Day::getPlanes0).sum(), monthDto.getPlanes_0());
        assertEquals(days.stream().mapToInt(Day::getAbsAltitude).sum() / total,
                monthDto.getAvg_altitude());
        assertEquals(days.stream().mapToInt(Day::getAbsSpeed).sum() / total,
                monthDto.getAvg_speed());
        assertEquals((days.stream().filter(Day::isLessThanThirtyPlanes).count()) / (float) days.size() * 100,
                monthDto.getDays_with_less_than_thirty_planes(), 0.01f);
        int[] monthDays = new int[date.getMonth().length(date.isLeapYear())];
        for (Day day : days) {
            monthDays[day.getDate().getDayOfMonth() - 1] = day.getTotal();
        }
        for (int i = 0; i < monthDays.length; i++) {
            log.info(i + ": " + monthDays[i] + " " + monthDto.getDays()[i]);
        }
        assertArrayEquals(monthDays, monthDto.getDays());
    }
}