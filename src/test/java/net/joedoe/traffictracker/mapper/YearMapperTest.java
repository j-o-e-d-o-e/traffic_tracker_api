package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class YearMapperTest {
    private final LocalDate startDate = LocalDate.now().withDayOfMonth(1).withMonth(1);
    private final List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfYear() - 1);

    @Test
    public void toDto() {
        YearDto yearDto = YearMapper.toDto(startDate, days, true, true);

        assertEquals(startDate, yearDto.getStart_date());
        int total = days.stream().mapToInt(Day::getTotal).sum();
        assertEquals(total, yearDto.getTotal());
        assertEquals(days.stream().mapToInt(Day::getFlights23).sum(), yearDto.getFlights_23());
        assertEquals(days.stream().mapToInt(Day::getFlights0).sum(), yearDto.getFlights_0());
        assertEquals(days.stream().mapToInt(Day::getAbsAltitude).sum() / total,
                yearDto.getAvg_altitude());
        assertEquals(days.stream().mapToInt(Day::getAbsSpeed).sum() / total,
                yearDto.getAvg_speed());
        assertEquals((days.stream().filter(Day::isLessThanThirtyFlights).count()) / (float) days.size() * 100,
                yearDto.getDays_with_less_than_thirty_flights(), 0.01f);
        Integer[] months = new Integer[12];
        for (Day day : days) {
            int i = day.getDate().getMonth().getValue() - 1;
            if (months[i] == null) months[i] = day.getTotal();
            else months[i] += day.getTotal();
        }

        assertArrayEquals(months, yearDto.getMonths());
        Integer[] avgFlights = new Integer[12];
        int avg = (int) (total / Arrays.stream(months).filter(Objects::nonNull).count());
        for (int i = 0; i < months.length; i++) if (months[i] != null) avgFlights[i] = avg;
        assertArrayEquals(avgFlights, yearDto.getAvg_flights());
    }
}