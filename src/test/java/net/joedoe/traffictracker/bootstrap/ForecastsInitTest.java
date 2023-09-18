package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastHour;
import net.joedoe.traffictracker.model.ForecastScore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ForecastsInitTest {

    public static List<ForecastDay> createForecastDays() {
        List<ForecastDay> list = new ArrayList<>();
        int days = 5;
        while (days >= 0) {
            list.add(createForecastDay(LocalDate.now().minusDays(days)));
            days--;
        }
        return list;
    }

    public static ForecastDay createForecastDay(LocalDate date) {
        ForecastDay day = new ForecastDay();
        day.setId((long) date.getDayOfYear());
        day.setDate(date);
        day.setProbability(88.2f);
        List<ForecastHour> hours = new ArrayList<>();
        for (int i = 6; i < 24; i = i + 3) {
            ForecastHour hour = new ForecastHour();
            hour.setId((long) date.getDayOfYear() + i);
            hour.setTime(LocalTime.of(i, 0));
            hour.setProbability(88.2f);
            hour.setWindDegree(180);
            hours.add(hour);
        }
        day.setHours(hours);
        return day;
    }


    public static ForecastScore createScore() {
        ForecastScore score = new ForecastScore();
        score.setId(1L);
        score.setPrecision(86.05f);
        score.setMeanAbsoluteError(16.67f);
        score.setConfusionMatrix(new int[][]{{39, 18}, {12, 111}});
        return score;
    }


    public static List<Integer> createIntFlights() {
        return List.of(1, 20, 127, 129, 124, 127, 144, 151, 123, 144, 318);
    }

    public static List<Integer> createIntWinds() {
        return List.of(116, 133, 127, 129, 124, 127, 144, 151, 123, 144, 318);
    }
}
