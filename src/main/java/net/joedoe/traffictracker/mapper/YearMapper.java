package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.YearDto;
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
public class YearMapper {
    private static LocalDate date;

    static {
        try {
            YearMapper.date = LocalDate.parse(PropertiesHandler.getProperties("src/main/resources/start-date.properties")
                    .getProperty("start-date"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YearDto toDto(LocalDate date, List<Day> days) {
        YearDto yearDto = new YearDto();
        yearDto.setStart_date(date);
        LocalDate endDate = date.plusYears(1).minusDays(1);
        yearDto.setEnd_date(endDate);
        yearDto.setNow(LocalDateTime.now());
        yearDto.setYear(date.getYear());
        if (date.getYear() == YearMapper.date.getYear())
            yearDto.setFirst_month(YearMapper.date.getMonthValue());
        else
            yearDto.setFirst_month(1);
        yearDto.setPrev(date.minusYears(1).plusDays(1).isAfter(YearMapper.date.withMonth(1).withDayOfMonth(1)));
        yearDto.setNext(endDate.isBefore(LocalDate.now()));

        int total = 0, flights23 = 0, flights0 = 0, absAltitude = 0, absSpeed = 0, absDaysWithLessThanThirtyFlights = 0;
        int[] months = new int[12];
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
            months[day.getDate().getMonthValue() - 1] += day.getTotal();
            if (day.getDeparturesTop().size() == 0) continue;
            DaysMapperUtil.incrementDepartures(day, departuresDto, departures);
        }
        yearDto.setTotal(total);
        yearDto.setAvg_flights(getAvgFlights(date, months, total));
        yearDto.setFlights_23(flights23);
        yearDto.setFlights_0(flights0);
        if (total != 0) {
            yearDto.setAvg_altitude(absAltitude / total);
            yearDto.setAvg_speed(absSpeed / total);
        }
        float percentage = (absDaysWithLessThanThirtyFlights / (float) days.size()) * 100;
        yearDto.setDays_with_less_than_thirty_flights(Math.round(percentage * 100) / 100f);
        yearDto.setMonths(months);
        yearDto.setDepartures(DaysMapperUtil.setDepartures(departuresDto));
        yearDto.setAirports(DaysMapperUtil.mapToList(departures, 5));
        return yearDto;
    }

    @NotNull
    private static Integer[] getAvgFlights(LocalDate date, int[] months, int total) {
        Integer[] avgFlights;
        int avgFlightsVal = (int) (total / Arrays.stream(months).filter(m -> m != 0).count());
        if (date.getYear() == YearMapper.date.getYear()) {
            avgFlights = new Integer[12];
            Arrays.fill(avgFlights, 0, YearMapper.date.getMonthValue() - 1, null);
            Arrays.fill(avgFlights, YearMapper.date.getMonthValue() - 1, avgFlights.length, avgFlightsVal);
        } else {
            if (LocalDate.now().getYear() == date.getYear()) {
                avgFlights = new Integer[LocalDate.now().getMonthValue()];
            } else {
                avgFlights = new Integer[12];
            }
            Arrays.fill(avgFlights, avgFlightsVal);
        }
        return avgFlights;
    }
}
