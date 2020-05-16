package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.PlaneDayController;
import net.joedoe.traffictracker.controller.PlaneMonthController;
import net.joedoe.traffictracker.controller.PlaneWeekController;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.model.Day;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@PropertySource("classpath:start-date.properties")
@Component
public class WeekMapper extends ResourceAssemblerSupport<List<Day>, WeekDto> {
    @Value("${startDate}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public WeekMapper() {
        super(PlaneWeekController.class, WeekDto.class);
    }

    @Override
    public WeekDto toResource(List<Day> days) {
        if (days.size() == 0) {
            return null;
        }
        LocalDate date = days.get(0).getDate();
        WeekDto weekDto = daysToWeekDto(date.with(DayOfWeek.MONDAY), days);
        LocalDate startDate = weekDto.getStart_date();

        weekDto.add(linkTo(methodOn(PlaneWeekController.class)
                .getWeekByDate(date)).withSelfRel());
        if (weekDto.isPrev()) {
            weekDto.add(linkTo(methodOn(PlaneWeekController.class)
                    .getWeekByDate(startDate.minusWeeks(1))).withRel("prev_week"));
        }
        if (weekDto.isNext()) {
            weekDto.add(linkTo(methodOn(PlaneWeekController.class)
                    .getWeekByDate(startDate.plusWeeks(1))).withRel("next_week"));
        }
        weekDto.add(linkTo(methodOn(PlaneDayController.class)
                .getDayByDate(startDate)).withRel("days"));
        weekDto.add(linkTo(methodOn(PlaneMonthController.class)
                .getMonthByDate(date.getYear(), date.getMonthValue())).withRel("month"));
        return weekDto;
    }

    private WeekDto daysToWeekDto(LocalDate date, List<Day> days) {
        int total = 0, planes23 = 0, planes0 = 0, absAltitude = 0, absSpeed = 0;
        boolean prev = date.minusDays(6).isAfter(this.date);
        LocalDate endDate = date.plusDays(6);
        boolean next = endDate.isBefore(LocalDate.now());
        int[] weekdays = new int[7];
        for (Day day : days) {
            total += day.getTotal();
            planes23 += day.getPlanes23();
            planes0 += day.getPlanes0();
            absAltitude += day.getAbsAltitude();
            absSpeed += day.getAbsSpeed();
            weekdays[day.getDate().getDayOfWeek().getValue() - 1] = day.getTotal();
        }
        int[] avgPlanes;
        if (LocalDate.now().isBefore(date.plusDays(7))) {
            avgPlanes = new int[LocalDate.now().getDayOfWeek().getValue()];
        } else {
            avgPlanes = new int[7];
        }
        Arrays.fill(avgPlanes, total / days.size());
        int avgAltitude = 0, avgSpeed = 0;
        if (total != 0) {
            avgAltitude = absAltitude / total;
            avgSpeed = absSpeed / total;
        }
        return new WeekDto(date, endDate, LocalDateTime.now(), date.getYear(), date.getMonthValue(), prev, next,
                total, avgPlanes, planes23, planes0, avgAltitude, avgSpeed, weekdays);
    }
}
