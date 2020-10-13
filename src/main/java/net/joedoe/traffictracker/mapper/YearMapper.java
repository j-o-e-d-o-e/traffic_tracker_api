package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.MonthController;
import net.joedoe.traffictracker.controller.YearController;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.YearDto;
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
public class YearMapper extends ResourceAssemblerSupport<List<Day>, YearDto> {
    private LocalDate date;

    public YearMapper() {
        super(YearController.class, YearDto.class);
        try {
            this.date = LocalDate.parse(PropertiesHandler.getProperties("src/main/resources/start-date.properties")
                    .getProperty("startDate"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public YearDto toResource(List<Day> days) {
        if (days.size() == 0) {
            return null;
        }
        LocalDate date = days.get(0).getDate();
        YearDto yearDto = daysToYearDTO(date.withMonth(1).withDayOfMonth(1), days);
        LocalDate startDate = yearDto.getStart_date();

        yearDto.add(linkTo(methodOn(YearController.class).getYearByDate(date.getYear())).withSelfRel());
        if (yearDto.isPrev())
            yearDto.add(linkTo(methodOn(YearController.class).getYearByDate(startDate.minusYears(1).getYear())).withRel("prev_year"));
        if (yearDto.isNext())
            yearDto.add(linkTo(methodOn(YearController.class).getYearByDate(startDate.plusYears(1).getYear())).withRel("next_year"));
        yearDto.add(linkTo(methodOn(MonthController.class).getMonthByDate(date.getYear(), date.getMonthValue())).withRel("months"));
        return yearDto;
    }

    private YearDto daysToYearDTO(LocalDate date, List<Day> days) {
        YearDto yearDto = new YearDto();
        yearDto.setStart_date(date);
        LocalDate endDate = date.plusYears(1).minusDays(1);
        yearDto.setEnd_date(endDate);
        yearDto.setNow(LocalDateTime.now());
        yearDto.setYear(date.getYear());
        if (date.getYear() == this.date.getYear())
            yearDto.setFirst_month(this.date.getMonthValue());
        else
            yearDto.setFirst_month(1);
        yearDto.setPrev(date.minusYears(1).plusDays(1).isAfter(this.date.withMonth(1).withDayOfMonth(1)));
        yearDto.setNext(endDate.isBefore(LocalDate.now()));

        int total = 0, planes23 = 0, planes0 = 0, absAltitude = 0, absSpeed = 0, absDaysWithLessThanThirtyPlanes = 0;
        int[] months = new int[12];
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
            months[day.getDate().getMonthValue() - 1] += day.getTotal();
            if (day.getDeparturesTop().size() == 0) continue;
            DaysMapperUtil.incrementDepartures(day, departuresDto, departures);
        }
        yearDto.setTotal(total);
        yearDto.setAvg_planes(getAvgPlanes(date, months, total));
        yearDto.setPlanes_23(planes23);
        yearDto.setPlanes_0(planes0);
        if (total != 0) {
            yearDto.setAvg_altitude(absAltitude / total);
            yearDto.setAvg_speed(absSpeed / total);
        }
        float percentage = (absDaysWithLessThanThirtyPlanes / (float) days.size()) * 100;
        yearDto.setDays_with_less_than_thirty_planes(Math.round(percentage * 100) / 100f);
        yearDto.setMonths(months);
        yearDto.setDepartures(DaysMapperUtil.setDepartures(departuresDto));
        yearDto.setAirports(DaysMapperUtil.mapToList(departures, 5));
        return yearDto;
    }

    @NotNull
    private Integer[] getAvgPlanes(LocalDate date, int[] months, int total) {
        Integer[] avgPlanes;
        int avgPlanesVal = (int) (total / Arrays.stream(months).filter(m -> m != 0).count());
        if (date.getYear() == this.date.getYear()) {
            avgPlanes = new Integer[12];
            Arrays.fill(avgPlanes, 0, this.date.getMonthValue() - 1, null);
            Arrays.fill(avgPlanes, this.date.getMonthValue() - 1, avgPlanes.length, avgPlanesVal);
        } else {
            if (LocalDate.now().getYear() == date.getYear()) {
                avgPlanes = new Integer[LocalDate.now().getMonthValue()];
            } else {
                avgPlanes = new Integer[12];
            }
            Arrays.fill(avgPlanes, avgPlanesVal);
        }
        return avgPlanes;
    }
}
