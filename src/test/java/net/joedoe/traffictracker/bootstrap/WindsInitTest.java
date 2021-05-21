package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.dto.WindDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WindsInitTest {

    public static List<WindDto> createWinds(LocalDate date) {
        ArrayList<WindDto> windDtos = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            windDtos.add(createWind(date, i));
        return windDtos;
    }

    public static WindDto createWind(LocalDate date, int hour) {
        WindDto windDto = new WindDto();
        windDto.setDateTime(LocalDateTime.of(date, LocalTime.of(hour, 30)));
        windDto.setDeg(180);
        windDto.setSpeed(6.84f);
        return windDto;
    }
}
