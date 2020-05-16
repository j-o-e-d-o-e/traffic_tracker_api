package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@Slf4j
public class YearMapperTest {
    private YearMapper mapper = new YearMapper();
    private LocalDate date = LocalDate.now().withDayOfMonth(1).withMonth(1);
    private List<Day> days;

    @Before
    public void setUp() {
        days = DaysInitTest.createDays(date)
                .stream().filter(d -> {
                    LocalDate date = d.getDate();
                    return (date.isEqual(this.date) || date.isAfter(this.date)) && date.isBefore(this.date.plusYears(1));
                })
                .collect(Collectors.toList());
    }

    @Test
    public void toResources() {
        YearDto yearDto = mapper.toResource(days);

        assertEquals(date, yearDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, yearDto.getTotal());
//        assertEquals(total / days.size(), yearDto.getAvg_planes());
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
        for (int i = 0; i < months.length; i++) {
            log.info(i + ": " + months[i] + " " + yearDto.getMonths()[i]);
        }
        assertArrayEquals(months, yearDto.getMonths());
    }
}