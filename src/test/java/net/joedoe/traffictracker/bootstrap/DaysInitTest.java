package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.Day;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DaysInitTest {

    public static List<Day> createDays(LocalDate date) {
        ArrayList<Day> days = new ArrayList<>();
        Day day1 = new Day();
        day1.setId(1L);
        day1.setDate(date);
        day1.setTotal(100);
        day1.setLessThanThirtyPlanes(false);
        day1.setPlanes23(10);
        day1.setPlanes0(10);
        day1.setAbsAltitude(100000);
        day1.setAbsSpeed(30000);
        days.add(day1);

        Day day2 = new Day();
        day2.setId(2L);
        day2.setDate(date.plusDays(1));
        day2.setTotal(200);
        day2.setLessThanThirtyPlanes(false);
        day2.setPlanes23(20);
        day2.setPlanes0(20);
        day2.setAbsAltitude(200000);
        day2.setAbsSpeed(60000);
        days.add(day2);

        Day day3 = new Day();
        day3.setId(3L);
        day3.setDate(date.plusWeeks(1));
        day3.setTotal(300);
        day3.setLessThanThirtyPlanes(false);
        day3.setPlanes23(30);
        day3.setPlanes0(30);
        day3.setAbsAltitude(300000);
        day3.setAbsSpeed(90000);
        days.add(day3);

        Day day4 = new Day();
        day4.setId(4L);
        day4.setDate(date.plusMonths(1));
        day4.setTotal(10);
        day4.setLessThanThirtyPlanes(true);
        day4.setPlanes23(3);
        day4.setPlanes0(0);
        day4.setAbsAltitude(300000);
        day4.setAbsSpeed(90000);
        days.add(day4);

        Day day5 = new Day();
        day5.setId(5L);
        day5.setDate(date.plusYears(1));
        day5.setTotal(20);
        day5.setLessThanThirtyPlanes(true);
        day5.setPlanes23(0);
        day5.setPlanes0(3);
        day5.setAbsAltitude(300000);
        day5.setAbsSpeed(90000);
        days.add(day5);

        return days;
    }
}
