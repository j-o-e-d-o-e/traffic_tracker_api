package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class ForecastDayDto {
    private LocalDate date;
    private float probability;
    private List<ForecastHourDto> hours = new ArrayList<>();

    public void addHour(ForecastHourDto hourDto) {
        hours.add(hourDto);
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @AllArgsConstructor
    public static class ForecastHourDto  {
        private LocalTime time;
        private int wind_degree;
        private float probability;
    }
}
