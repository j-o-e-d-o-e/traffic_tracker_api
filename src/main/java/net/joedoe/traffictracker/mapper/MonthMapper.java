package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.MonthController;
import net.joedoe.traffictracker.controller.WeekController;
import net.joedoe.traffictracker.controller.YearController;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.utils.PropertiesHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@Component
public class MonthMapper extends ResourceAssemblerSupport<List<Day>, MonthDto> {
    private LocalDate date;

    public MonthMapper() {
        super(MonthController.class, MonthDto.class);
        try {
            this.date = LocalDate.parse(PropertiesHandler.getProperties("src/main/resources/start-date.properties")
                    .getProperty("startDate"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MonthDto toResource(List<Day> days) {
        if (days.size() == 0)
            return null;
        LocalDate date = days.get(0).getDate();
        MonthDto monthDto = daysToMonthDTO(date.withDayOfMonth(1), days);
        LocalDate startDate = monthDto.getStart_date();

        monthDto.add(linkTo(methodOn(MonthController.class).getMonthByDate(date.getYear(), date.getMonthValue())).withSelfRel());
        if (monthDto.isPrev()) {
            LocalDate tmp = startDate.minusMonths(1);
            monthDto.add(linkTo(methodOn(MonthController.class).getMonthByDate(tmp.getYear(), tmp.getMonthValue())).withRel("prev_month"));
        }
        if (monthDto.isNext()) {
            LocalDate tmp = startDate.plusMonths(1);
            monthDto.add(linkTo(methodOn(MonthController.class).getMonthByDate(tmp.getYear(), tmp.getMonthValue())).withRel("next_month"));
        }
        monthDto.add(linkTo(methodOn(WeekController.class).getWeekByDate(date)).withRel("weeks"));
        monthDto.add(linkTo(methodOn(YearController.class).getYearByDate(date.getYear())).withRel("year"));
        return monthDto;
    }

    private MonthDto daysToMonthDTO(LocalDate date, List<Day> days) {
        MonthDto monthDto = new MonthDto();
        monthDto.setStart_date(date);
        int daysOfMonth = date.getMonth().length(date.isLeapYear());
        LocalDate endDate = date.plusDays(daysOfMonth - 1);
        monthDto.setEnd_date(endDate);
        monthDto.setNow(LocalDateTime.now());
        monthDto.setYear(date.getYear());
        monthDto.setMonth(date.getMonthValue());
        if (date.getYear() == this.date.getYear() && date.getMonth() == this.date.getMonth())
            monthDto.setFirst_day_of_month(this.date);
        else
            monthDto.setFirst_day_of_month(date);
        monthDto.setPrev(date.minusMonths(1).plusDays(1).isAfter(this.date.withDayOfMonth(1)));
        monthDto.setNext(endDate.isBefore(LocalDate.now()));

        int total = 0, planes23 = 0, planes0 = 0, absAltitude = 0, absSpeed = 0, absDaysWithLessThanThirtyPlanes = 0;
        int[] monthDays = new int[daysOfMonth];
        DeparturesDto departuresDto = new DeparturesDto();
        Map<String, Integer> departures = new HashMap<>();
        for (Day day : days) {
            total += day.getTotal();
            planes23 += day.getPlanes23();
            planes0 += day.getPlanes0();
            absAltitude += day.getAbsAltitude();
            absSpeed += day.getAbsSpeed();
            if (day.isLessThanThirtyPlanes())
                absDaysWithLessThanThirtyPlanes += 1;
            monthDays[day.getDate().getDayOfMonth() - 1] = day.getTotal();
            if (day.getDeparturesTop().size()== 0) continue;
            DaysMapperUtil.incrementDepartures(day, departuresDto, departures);
        }
        monthDto.setTotal(total);
        monthDto.setAvg_planes(getAvgPlanes(date, days, total));
        monthDto.setPlanes_23(planes23);
        monthDto.setPlanes_0(planes0);
        if (total != 0) {
            monthDto.setAvg_altitude(absAltitude / total);
            monthDto.setAvg_speed(absSpeed / total);
        }
        float percentage = (absDaysWithLessThanThirtyPlanes / (float) days.size()) * 100;
        monthDto.setDays_with_less_than_thirty_planes(Math.round(percentage * 100) / 100f);
        monthDto.setDays(monthDays);
        monthDto.setDepartures(DaysMapperUtil.setDepartures(departuresDto));
        monthDto.setAirports(DaysMapperUtil.mapToList(departures, 5));
        return monthDto;
    }

    @NotNull
    private Integer[] getAvgPlanes(LocalDate date, List<Day> days, int total) {
        Integer[] avgPlanes;
        // Sept 2019
        if (date.getYear() == this.date.getYear() && date.getMonth().equals(this.date.getMonth())) {
            avgPlanes = new Integer[this.date.getMonth().length(this.date.isLeapYear())];
            Arrays.fill(avgPlanes, 0, this.date.getDayOfMonth() - 1, null);
            Arrays.fill(avgPlanes, this.date.getDayOfMonth() - 1, avgPlanes.length, total / days.size());
        } else {
            if (LocalDate.now().getMonth().equals(date.getMonth())) { // current month (now)
                avgPlanes = new Integer[LocalDate.now().getDayOfMonth()];
            } else { // earlier month (but not Sept 2019)
                avgPlanes = new Integer[date.getMonth().length(date.isLeapYear())];
            }
            Arrays.fill(avgPlanes, total / days.size());
        }
        return avgPlanes;
    }
}
