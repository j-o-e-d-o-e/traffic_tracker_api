package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.model.Day;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class DayMapperTest {
    private DayMapper mapper = new DayMapper();
    private LocalDate date = LocalDate.now();
    private Day day = DaysInitTest.createDays(date).get(0);

    @Test
    public void dayToResource() {
        DayDto dayDto = mapper.toResource(day);

        assertEquals(day.getDate(), dayDto.getDate());
        assertEquals(day.getTotal(), dayDto.getTotal());
        assertEquals(day.getAvgAltitude(), dayDto.getAvg_altitude());
        assertEquals(day.getAvgSpeed(), dayDto.getAvg_speed());
        assertEquals(day.getWindSpeed(), dayDto.getWind_speed(), 0.01f);
        assertArrayEquals(day.getHoursPlane(), dayDto.getHours_plane());
        assertArrayEquals(day.getHoursWind(), dayDto.getHours_wind());
    }
}