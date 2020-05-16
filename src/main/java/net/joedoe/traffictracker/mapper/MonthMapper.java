package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.PlaneMonthController;
import net.joedoe.traffictracker.controller.PlaneWeekController;
import net.joedoe.traffictracker.controller.PlaneYearController;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.model.Day;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@PropertySource("classpath:start-date.properties")
@Component
public class MonthMapper extends ResourceAssemblerSupport<List<Day>, MonthDto> {
    @Value("${startDate}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public MonthMapper() {
        super(PlaneMonthController.class, MonthDto.class);
    }

    @Override
    public MonthDto toResource(List<Day> days) {
        if (days.size() == 0) {
            return null;
        }
        LocalDate date = days.get(0).getDate();
        MonthDto monthDto = daysToMonthDTO(date.withDayOfMonth(1), days);
        LocalDate startDate = monthDto.getStart_date();

        monthDto.add(linkTo(methodOn(PlaneMonthController.class)
                .getMonthByDate(date.getYear(), date.getMonthValue())).withSelfRel());
        if (monthDto.isPrev()) {
            LocalDate tmp = startDate.minusMonths(1);
            monthDto.add(linkTo(methodOn(PlaneMonthController.class)
                    .getMonthByDate(tmp.getYear(), tmp.getMonthValue())).withRel("prev_month"));
        }
        if (monthDto.isNext()) {
            LocalDate tmp = startDate.plusMonths(1);
            monthDto.add(linkTo(methodOn(PlaneMonthController.class)
                    .getMonthByDate(tmp.getYear(), tmp.getMonthValue())).withRel("next_month"));
        }
        monthDto.add(linkTo(methodOn(PlaneWeekController.class)
                .getWeekByDate(date)).withRel("weeks"));
        monthDto.add(linkTo(methodOn(PlaneYearController.class)
                .getYearByDate(date.getYear())).withRel("year"));
        return monthDto;
    }

    private MonthDto daysToMonthDTO(LocalDate date, List<Day> days) {
        int total = 0, planes23 = 0, planes0 = 0, absAltitude = 0, absSpeed = 0, absDaysWithLessThanThirtyPlanes = 0;
        int daysOfMonth = date.getMonth().length(date.isLeapYear());
        boolean prev = date.minusMonths(1).plusDays(1).isAfter(this.date.withDayOfMonth(1));
        LocalDate endDate = date.plusDays(daysOfMonth - 1);
        boolean next = endDate.isBefore(LocalDate.now());
        LocalDate firstDayOfMonth;
        if (date.getMonth() == this.date.getMonth()) {
            firstDayOfMonth = this.date;
        } else {
            firstDayOfMonth = date;
        }
        int[] monthDays = new int[daysOfMonth];
        for (Day day : days) {
            total += day.getTotal();
            planes23 += day.getPlanes23();
            planes0 += day.getPlanes0();
            absAltitude += day.getAbsAltitude();
            absSpeed += day.getAbsSpeed();
            if (day.isLessThanThirtyPlanes())
                absDaysWithLessThanThirtyPlanes += 1;
            monthDays[day.getDate().getDayOfMonth() - 1] = day.getTotal();
        }
        Integer[] avgPlanes;
        if (date.getMonth().equals(this.date.getMonth())) {
            avgPlanes = new Integer[this.date.getMonth().length(this.date.isLeapYear())];
            Arrays.fill(avgPlanes, 0, this.date.getDayOfMonth() - 1, null);
            Arrays.fill(avgPlanes, this.date.getDayOfMonth() - 1, avgPlanes.length, total / days.size());
        } else {
            if (LocalDate.now().getMonth().equals(date.getMonth())) {
                avgPlanes = new Integer[LocalDate.now().getDayOfMonth()];
            } else {
                avgPlanes = new Integer[date.getMonth().length(date.isLeapYear())];
            }
            Arrays.fill(avgPlanes, total / days.size());
        }
        float percentage = (absDaysWithLessThanThirtyPlanes / (float) days.size()) * 100;
        float daysWithLessThanThirtyPlanes = Math.round(percentage * 100) / 100f;
        int avgAltitude = 0, avgSpeed = 0;
        if (total != 0) {
            avgAltitude = absAltitude / total;
            avgSpeed = absSpeed / total;
        }
        return new MonthDto(date, endDate, LocalDateTime.now(), date.getYear(), date.getMonthValue(), firstDayOfMonth, prev, next,
                total, avgPlanes, planes23, planes0, avgAltitude, avgSpeed, daysWithLessThanThirtyPlanes, monthDays);
    }
}
