package net.joedoe.traffictracker.mapper;

import jakarta.annotation.Nonnull;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class MonthMapper {
    private static final LocalDate date = LocalDate.of(2019, 9, 9);

    public static MonthDto toDto(LocalDate date, List<Day> days, boolean prev, boolean next) {
        MonthDto monthDto = new MonthDto();
        monthDto.setStart_date(date);
        int daysOfMonth = date.getMonth().length(date.isLeapYear());
        LocalDate endDate = date.plusDays(daysOfMonth - 1);
        monthDto.setEnd_date(endDate);
        days.sort(Comparator.comparing(Day::getDate));
        monthDto.setFirst_week(days.get(0).getDate());
        monthDto.setNow(LocalDateTime.now());
        monthDto.setYear(date.getYear());
        monthDto.setMonth(date.getMonthValue());
        if (date.getYear() == MonthMapper.date.getYear() && date.getMonth() == MonthMapper.date.getMonth())
            monthDto.setFirst_day_of_month(MonthMapper.date);
        else monthDto.setFirst_day_of_month(date);
        monthDto.setPrev(prev);
        monthDto.setNext(next);

        int total = 0, flights23 = 0, flights0 = 0, absAltitude = 0, absSpeed = 0, absDaysWithLessThanThirtyFlights = 0;
        Integer[] monthDays = new Integer[daysOfMonth];
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
            if (day.getDeparturesTop().isEmpty()) continue;
            DaysMapperUtil.incrementDepartures(day, departuresDto, departures);
        }
        monthDto.setTotal(total);
        monthDto.setAvg_flights(getAvgFlights(days, total));
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

    @Nonnull
    private static Integer[] getAvgFlights(List<Day> days, int total) {
        LocalDate date = days.get(0).getDate();
        Integer[] avgFlights = new Integer[date.getMonth().length(date.isLeapYear())];
        int avg = total / days.size();
        for (Day day : days) avgFlights[day.getDate().getDayOfMonth() - 1] = avg;
        return avgFlights;
    }
}
