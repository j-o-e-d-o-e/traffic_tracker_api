package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.bootstrap.ForecastsInitTest;
import net.joedoe.traffictracker.dto.ForecastDayDto;
import net.joedoe.traffictracker.model.ForecastDay;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ForecastMapperTest {

    @Test
    public void toResources() {
        List<ForecastDay> days = ForecastsInitTest.createForecastDays();
        ForecastDay day = days.get(0);

        List<ForecastDayDto> dayDtos = ForecastMapper.toResources(days);
        ForecastDayDto dayDto = dayDtos.get(0);
        assertEquals(days.size(), dayDtos.size());
        assertEquals(day.getProbability(), dayDto.getProbability(), 0.1f);
        assertEquals(day.getHours().size(), dayDto.getHours().size());
        float exp = day.getHours().get(0).getProbability();
        float act = dayDto.getHours().get(0).getProbability();
        assertEquals(exp, act, 0.1f);
    }
}