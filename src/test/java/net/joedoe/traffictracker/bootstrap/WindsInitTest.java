package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.Wind;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WindsInitTest {

    public static List<Wind> createWinds(LocalDate date) {
        ArrayList<Wind> winds = new ArrayList<>();

        Wind wind1 = new Wind();
        wind1.setDate(LocalDateTime.of(date, LocalTime.now().withHour(6).withMinute(30)));
        wind1.setDeg(180);
        wind1.setSpeed(6.84f);
        winds.add(wind1);

        Wind wind2 = new Wind();
        wind2.setDate(LocalDateTime.of(date, LocalTime.now().withHour(7).withMinute(30)));
        wind2.setDeg(180);
        wind2.setSpeed(6.84f);
        winds.add(wind2);

        Wind wind3 = new Wind();
        wind3.setDate(LocalDateTime.of(date, LocalTime.now().withHour(8).withMinute(30)));
        wind3.setDeg(180);
        wind3.setSpeed(6.84f);
        winds.add(wind3);

        return winds;
    }
}
