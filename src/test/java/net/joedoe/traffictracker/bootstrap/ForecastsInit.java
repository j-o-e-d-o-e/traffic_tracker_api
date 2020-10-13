package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastHour;
import net.joedoe.traffictracker.model.ForecastScore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForecastsInit {

    public static List<ForecastDay> createDays() {
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
}
