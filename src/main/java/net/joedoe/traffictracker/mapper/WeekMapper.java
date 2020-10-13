package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.DayController;
import net.joedoe.traffictracker.controller.MonthController;
import net.joedoe.traffictracker.controller.WeekController;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.utils.PropertiesHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@Component
public class WeekMapper extends ResourceAssemblerSupport<List<Day>, WeekDto> {
    private LocalDate date;

    public WeekMapper() {
        super(WeekController.class, WeekDto.class);
        try {
            this.date = LocalDate.parse(PropertiesHandler.getProperties("src/main/resources/start-date.properties").getProperty("startDate"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public WeekDto toResource(List<Day> days) {
        if (days.size() == 0)
            return null;
        LocalDate date = days.get(0).getDate();
        WeekDto weekDto = daysToWeekDto(date.with(DayOfWeek.MONDAY), days);
        LocalDate startDate = weekDto.getStart_date();

        weekDto.add(linkTo(methodOn(WeekController.class).getWeekByDate(date)).withSelfRel());
        if (weekDto.isPrev())
            weekDto.add(linkTo(methodOn(WeekController.class).getWeekByDate(startDate.minusWeeks(1))).withRel("prev_week"));
        if (weekDto.isNext())
            weekDto.add(linkTo(methodOn(WeekController.class).getWeekByDate(startDate.plusWeeks(1))).withRel("next_week"));
        weekDto.add(linkTo(methodOn(DayController.class).getDayByDate(startDate)).withRel("days"));
        weekDto.add(linkTo(methodOn(MonthController.class).getMonthByDate(date.getYear(), date.getMonthValue())).withRel("month"));
        return weekDto;
    }

    private WeekDto daysToWeekDto(LocalDate date, List<Day> days) {
        WeekDto weekDto = new WeekDto();
        weekDto.setStart_date(date);
        LocalDate endDate = date.plusDays(6);
        weekDto.setEnd_date(date.plusDays(6));
        weekDto.setNow(LocalDateTime.now());
        weekDto.setYear(date.getYear());
        weekDto.setMonth(date.getMonthValue());
        weekDto.setPrev(date.minusDays(6).isAfter(this.date));
        weekDto.setNext(endDate.isBefore(LocalDate.now()));

        int total = 0, planes23 = 0, planes0 = 0, absAltitude = 0, absSpeed = 0;
        int[] weekdays = new int[7];
        DeparturesDto departuresDto = new DeparturesDto();
        Map<String, Integer> departures = new HashMap<>();
        for (Day day : days) {
            total += day.getTotal();
            planes23 += day.getPlanes23();
            planes0 += day.getPlanes0();
            absAltitude += day.getAbsAltitude();
            absSpeed += day.getAbsSpeed();
            weekdays[day.getDate().getDayOfWeek().getValue() - 1] = day.getTotal();
            if (day.getDeparturesTop().size() == 0) continue;
            DaysMapperUtil.incrementDepartures(day, departuresDto, departures);
        }
        weekDto.setTotal(total);
        weekDto.setAvg_planes(getAvgPlanes(date, days, total));
        weekDto.setPlanes_23(planes23);
        weekDto.setPlanes_0(planes0);
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
    private int[] getAvgPlanes(LocalDate date, List<Day> days, int total) {
        int[] avgPlanes;
        if (LocalDate.now().isBefore(date.plusDays(7))) { // current week
            avgPlanes = new int[LocalDate.now().getDayOfWeek().getValue()];
        } else {
            avgPlanes = new int[7];
        }
        Arrays.fill(avgPlanes, total / days.size());
        return avgPlanes;
    }
}
