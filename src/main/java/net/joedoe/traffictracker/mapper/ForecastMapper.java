package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.dto.ForecastDayDto;
import net.joedoe.traffictracker.dto.ForecastDayDto.ForecastHourDto;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastHour;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ForecastMapper {
    public static List<ForecastDayDto> toResources(List<ForecastDay> days) {
        if (days == null || days.isEmpty()) return null;
        List<ForecastDayDto> dayDtos = new ArrayList<>();
        for (ForecastDay day : days) {
            ForecastDayDto dayDto = new ForecastDayDto();
            dayDto.setDate(day.getDate());
            dayDto.setProbability(day.getProbability());
            for (ForecastHour hour : day.getHours()) {
                ForecastHourDto hourDto = new ForecastHourDto(hour.getTime(), hour.getWindDegree(), hour.getProbability());
                dayDto.addHour(hourDto);
            }
            dayDtos.add(dayDto);
        }
        return dayDtos;
    }
}
