package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.Wind;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WindsInit {

    public static List<Wind> createWinds(LocalDate date) {
        ArrayList<Wind> winds = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            winds.add(createWind(date, i));
        return winds;
    }

    public static Wind createWind(LocalDate date, int hour) {
        Wind wind = new Wind();
        wind.setId((long) hour);
        wind.setDate(LocalDateTime.of(date, LocalTime.of(hour, 30)));
        wind.setDeg(180);
        wind.setSpeed(6.84f);
        return wind;
    }
}
