package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.model.Day;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class WeekMapper {

    public static WeekDto toDto(LocalDate date, List<Day> days, boolean prev, boolean next) {
        WeekDto weekDto = new WeekDto();
        weekDto.setStart_date(date);
        weekDto.setEnd_date(date.plusDays(6));
        days.sort(Comparator.comparing(Day::getDate));
        weekDto.setFirst_day(days.get(0).getDate());
        weekDto.setNow(LocalDateTime.now());
        weekDto.setYear(date.getYear());
        weekDto.setMonth(date.getMonthValue());
        weekDto.setPrev(prev);
        weekDto.setNext(next);

        int total = 0, flights23 = 0, flights0 = 0, absAltitude = 0, absSpeed = 0;
        int[] weekdays = new int[7];
        DeparturesDto departuresDto = new DeparturesDto();
        Map<String, Integer> departures = new HashMap<>();
        for (Day day : days) {
            total += day.getTotal();
            flights23 += day.getFlights23();
            flights0 += day.getFlights0();
            absAltitude += day.getAbsAltitude();
            absSpeed += day.getAbsSpeed();
            weekdays[day.getDate().getDayOfWeek().getValue() - 1] = day.getTotal();
            if (day.getDeparturesTop().size() == 0) continue;
            DaysMapperUtil.incrementDepartures(day, departuresDto, departures);
        }
        weekDto.setTotal(total);
        weekDto.setAvg_flights(getAvgFlights(date, days.size(), total));
        weekDto.setFlights_23(flights23);
        weekDto.setFlights_0(flights0);
        if (total != 0) {
            weekDto.setAvg_altitude(absAltitude / total);
            weekDto.setAvg_speed(absSpeed / total);
        }
        weekDto.setWeekdays(weekdays);
        weekDto.setDepartures(DaysMapperUtil.setDepartures(departuresDto));
        weekDto.setAirports(DaysMapperUtil.mapToList(departures, 5));
        return weekDto;
    }

    @NotNull
    private static int[] getAvgFlights(LocalDate date, int daysSize, int total) {
        int[] avgFlights;
        if (LocalDate.now().isBefore(date.plusDays(7))) { // current week
            avgFlights = new int[LocalDate.now().getDayOfWeek().getValue()];
        } else {
            avgFlights = new int[7];
        }
        Arrays.fill(avgFlights, total / daysSize);
        return avgFlights;
    }
}
