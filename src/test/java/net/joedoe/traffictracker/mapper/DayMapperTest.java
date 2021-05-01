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

    @Test
    public void toDayDtoToday() {
        Day day = DaysInit.createDay(LocalDate.now());

        DayDto dayDto = DayMapper.toDto(day);

        assertEquals(day.getDate(), dayDto.getDate());
        assertEquals(day.getTotal(), dayDto.getTotal());
        assertEquals(day.getAvgAltitude(), dayDto.getAvg_altitude());
        assertEquals(day.getAvgSpeed(), dayDto.getAvg_speed());
        assertEquals(day.getWindSpeed(), dayDto.getWind_speed(), 0.01f);
        assertEquals( LocalDateTime.now().getHour() + 1, dayDto.getHours_flight().length);
        assertEquals(LocalDateTime.now().getHour() + 1, dayDto.getHours_wind().length);
//        log.info(String.valueOf(dayDto.getDepartures()));
    }


    @Test
    public void toDayDtoYesterday() {
        Day day = DaysInit.createDay(LocalDate.now().minusDays(1));

        DayDto dayDto = DayMapper.toDto(day);

        assertEquals(day.getDate(), dayDto.getDate());
        assertEquals(day.getTotal(), dayDto.getTotal());
        assertEquals(day.getAvgAltitude(), dayDto.getAvg_altitude());
        assertEquals(day.getAvgSpeed(), dayDto.getAvg_speed());
        assertEquals(day.getWindSpeed(), dayDto.getWind_speed(), 0.01f);
        assertEquals(24, dayDto.getHours_flight().length);
        assertEquals(24, dayDto.getHours_wind().length);
    }
}