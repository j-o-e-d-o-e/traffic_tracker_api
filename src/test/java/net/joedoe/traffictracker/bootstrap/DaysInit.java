package net.joedoe.traffictracker.bootstrap;

import net.joedoe.traffictracker.model.Day;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaysInit {

    public static List<Day> createDays(int days){
        List<Day> list = new ArrayList<>();
        while (days >= 0) {
            list.add(createDay(LocalDate.now().minusDays(days)));
            days--;
        }
        return list;
    }

    public static Day createDay(LocalDate date) {
        Day day = new Day();
        day.setId((long) date.getDayOfYear());
        day.setDate(date);
        day.setTotal(100);
        day.setLessThanThirtyPlanes(false);
        day.setPlanes23(10);
        day.setPlanes0(10);
        day.setAvgAltitude(930);
        day.setAvgSpeed(326);
        day.setHoursPlane(new int[]{0, 0, 0, 0, 0, 0, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23});
        day.setPlanes(PlanesInit.createPlanes(day));
        day.setWindSpeed(17.64f);
        day.setHoursWind(new int[]{0, 0, 0, 0, 0, 0, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23});
        day.setAbsAltitude(100000);
        day.setAbsSpeed(30000);
        day.setAbsWind(19);
        day.setAbsWindSpeed(200f);
        day.setDeparturesContinental(0.0f);
        day.setDeparturesInternational(0.54f);
        day.setDeparturesNational(0.29f);
        day.setDeparturesUnknown(0.17f);
        day.setDeparturesContinentalAbs(0);
        day.setDeparturesInternationalAbs(47);
        day.setDeparturesNationalAbs(25);
        day.setDeparturesUnknownAbs(17);
        Map<String, Integer> departuresTop = new HashMap<>();
        departuresTop.put("ABCD", 8);
        departuresTop.put("EFGH", 6);
        departuresTop.put("IJKL", 4);
        departuresTop.put("MNOP", 4);
        departuresTop.put("QRST", 4);
        day.setDeparturesTop(departuresTop);
        return day;
    }
}
