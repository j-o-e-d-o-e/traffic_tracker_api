package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.util.PropertiesHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class MonthMapper {
    private static LocalDate date;

    static {
        try {
            MonthMapper.date = LocalDate.parse(PropertiesHandler.getProperties("src/main/resources/start-date.properties")
                    .getProperty("startDate"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MonthDto toDto(LocalDate date, List<Day> days) {
        MonthDto monthDto = new MonthDto();
        monthDto.setStart_date(date);
        int daysOfMonth = date.getMonth().length(date.isLeapYear());
        LocalDate endDate = date.plusDays(daysOfMonth - 1);
        monthDto.setEnd_date(endDate);
        monthDto.setNow(LocalDateTime.now());
        monthDto.setYear(date.getYear());
        monthDto.setMonth(date.getMonthValue());
        if (date.getYear() == MonthMapper.date.getYear() && date.getMonth() == MonthMapper.date.getMonth())
            monthDto.setFirst_day_of_month(MonthMapper.date);
        else
            monthDto.setFirst_day_of_month(date);
        monthDto.setPrev(date.minusMonths(1).plusDays(1).isAfter(MonthMapper.date.withDayOfMonth(1)));
        monthDto.setNext(endDate.isBefore(LocalDate.now()));

        int total = 0, flights23 = 0, flights0 = 0, absAltitude = 0, absSpeed = 0, absDaysWithLessThanThirtyFlights = 0;
        int[] monthDays = new int[daysOfMonth];
        DeparturesDto departuresDto = new DeparturesDto();
        Map<String, Integer> departures = new HashMap<>();
        for (Day day : days) {
            total += day.getTotal();
            flights23 += day.getFlights23();
            flights0 += day.getFlights0();
            absAltitude += day.getAbsAltitude();
            absSpeed += day.getAbsSpeed();
            if (day.isLessThanThirtyFlights())
                absDaysWithLessThanThirtyFlights += 1;
            monthDays[day.getDate().getDayOfMonth() - 1] = day.getTotal();
            if (day.getDeparturesTop().size() == 0) continue;
            DaysMapperUtil.incrementDepartures(day, departuresDto, departures);
        }
        monthDto.setTotal(total);
        monthDto.setAvg_flights(getAvgFlights(date, days, total));
        monthDto.setFlights_23(flights23);
        monthDto.setFlights_0(flights0);
        if (total != 0) {
            monthDto.setAvg_altitude(absAltitude / total);
            monthDto.setAvg_speed(absSpeed / total);
        }
        float percentage = (absDaysWithLessThanThirtyFlights / (float) days.size()) * 100;
        monthDto.setDays_with_less_than_thirty_flights(Math.round(percentage * 100) / 100f);
        monthDto.setDays(monthDays);
        monthDto.setDepartures(DaysMapperUtil.setDepartures(departuresDto));
        monthDto.setAirports(DaysMapperUtil.mapToList(departures, 5));
        return monthDto;
    }

    @NotNull
    private static Integer[] getAvgFlights(LocalDate date, List<Day> days, int total) {
        Integer[] avgFlights;
        // Sept 2019
        if (date.getYear() == MonthMapper.date.getYear() && date.getMonth().equals(MonthMapper.date.getMonth())) {
            avgFlights = new Integer[MonthMapper.date.getMonth().length(MonthMapper.date.isLeapYear())];
            Arrays.fill(avgFlights, 0, MonthMapper.date.getDayOfMonth() - 1, null);
            Arrays.fill(avgFlights, MonthMapper.date.getDayOfMonth() - 1, avgFlights.length, total / days.size());
        } else {
            LocalDate now = LocalDate.now();
            if (now.getYear() == date.getYear() && now.getMonth().equals(date.getMonth())) { // current month
                avgFlights = new Integer[LocalDate.now().getDayOfMonth()];
            } else { // earlier month (but not Sept 2019)
                avgFlights = new Integer[date.getMonth().length(date.isLeapYear())];
            }
            Arrays.fill(avgFlights, total / days.size());
        }
        return avgFlights;
    }
}
