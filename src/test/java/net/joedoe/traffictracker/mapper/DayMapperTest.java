package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

@Slf4j
public class DayMapperTest {
    private final DayMapper mapper = new DayMapper();

    @Test
    public void toResourceToday(){
        Day day = DaysInit.createDay(LocalDate.now());
        LocalDate date = day.getDate();

        DayDto dayDto = mapper.toResource(day);

        assertEquals("/planes/day/" + date.minusDays(1), dayDto.getLink("prev_day").getHref());
        assertFalse(dayDto.hasLink("next_day"));
        assertEquals("/planes/" + date, dayDto.getLink("planes").getHref());
        assertEquals("/planes/week/" + date, dayDto.getLink("week").getHref());
    }

    @Test
    public void dayToDayDtoToday() {
        Day day = DaysInit.createDay(LocalDate.now());

        DayDto dayDto = mapper.dayToDayDto(day);

        assertEquals(day.getDate(), dayDto.getDate());
        assertEquals(day.getTotal(), dayDto.getTotal());
        assertEquals(day.getAvgAltitude(), dayDto.getAvg_altitude());
        assertEquals(day.getAvgSpeed(), dayDto.getAvg_speed());
        assertEquals(day.getWindSpeed(), dayDto.getWind_speed(), 0.01f);
        assertEquals( LocalDateTime.now().getHour() + 1, dayDto.getHours_plane().length);
        assertEquals(LocalDateTime.now().getHour() + 1, dayDto.getHours_wind().length);
        log.info(String.valueOf(dayDto.getDepartures()));
    }
    @Test
    public void toResourceYesterday(){
        Day day = DaysInit.createDay(LocalDate.now().minusDays(1));
        LocalDate date = day.getDate();

        DayDto dayDto = mapper.toResource(day);

        assertEquals("/planes/day/" + date.minusDays(1), dayDto.getLink("prev_day").getHref());
        assertEquals("/planes/day/" + date.plusDays(1), dayDto.getLink("next_day").getHref());
        assertEquals("/planes/" + date, dayDto.getLink("planes").getHref());
        assertEquals("/planes/week/" + date, dayDto.getLink("week").getHref());
    }

    @Test
    public void dayToDayDtoYesterday() {
        Day day = DaysInit.createDay(LocalDate.now().minusDays(1));

        DayDto dayDto = mapper.dayToDayDto(day);

        assertEquals(day.getDate(), dayDto.getDate());
        assertEquals(day.getTotal(), dayDto.getTotal());
        assertEquals(day.getAvgAltitude(), dayDto.getAvg_altitude());
        assertEquals(day.getAvgSpeed(), dayDto.getAvg_speed());
        assertEquals(day.getWindSpeed(), dayDto.getWind_speed(), 0.01f);
        assertEquals(24, dayDto.getHours_plane().length);
        assertEquals(24, dayDto.getHours_wind().length);
    }
}