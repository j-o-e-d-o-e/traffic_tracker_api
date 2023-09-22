package net.joedoe.traffictracker.mapper;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.model.Day;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class YearMapper {
    private static final LocalDate date = LocalDate.of(2019, 9, 9);

    public static YearDto toDto(LocalDate date, List<Day> days, boolean prev, boolean next) {
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
        yearDto.setPrev(prev);
        yearDto.setNext(next);

        int total = 0, flights23 = 0, flights0 = 0, absAltitude = 0, absSpeed = 0, absDaysWithLessThanThirtyFlights = 0;
        Integer[] months = new Integer[12];
        DeparturesDto departuresDto = new DeparturesDto();
        Map<String, Integer> departures = new HashMap<>();
        for (Day day : days) {
            total += day.getTotal();
            flights23 += day.getFlights23();
            flights0 += day.getFlights0();
            absAltitude += day.getAbsAltitude();
            absSpeed += day.getAbsSpeed();
            if (day.isLessThanThirtyFlights()) absDaysWithLessThanThirtyFlights += 1;
            int i = day.getDate().getMonthValue() - 1;
            if (months[i] == null) months[i] = day.getTotal();
            else months[i] += day.getTotal();
            if (day.getDeparturesTop().isEmpty()) continue;
            DaysMapperUtil.incrementDepartures(day, departuresDto, departures);
        }
        yearDto.setTotal(total);
        yearDto.setAvg_flights(getAvgFlights(months, total));
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

    @Nonnull
    private static Integer[] getAvgFlights(Integer[] months, int total) {
        Integer[] avgFlights = new Integer[12];
        int avg = (int) (total / Arrays.stream(months).filter(Objects::nonNull).count());
        for (int i = 0; i < months.length; i++) if (months[i] != null) avgFlights[i] = avg;
        return avgFlights;
    }
}
