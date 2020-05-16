package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.PlaneMonthController;
import net.joedoe.traffictracker.controller.PlaneYearController;
import net.joedoe.traffictracker.dto.YearDto;
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
public class YearMapper extends ResourceAssemblerSupport<List<Day>, YearDto> {
    @Value("${startDate}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public YearMapper() {
        super(PlaneYearController.class, YearDto.class);
    }

    @Override
    public YearDto toResource(List<Day> days) {
        if (days.size() == 0) {
            return null;
        }
        LocalDate date = days.get(0).getDate();
        YearDto yearDto = daysToYearDTO(date.withMonth(1).withDayOfMonth(1), days);
        LocalDate startDate = yearDto.getStart_date();

        yearDto.add(linkTo(methodOn(PlaneYearController.class)
                .getYearByDate(date.getYear())).withSelfRel());
        if (yearDto.isPrev()) {
            yearDto.add(linkTo(methodOn(PlaneYearController.class)
                    .getYearByDate(startDate.minusYears(1).getYear())).withRel("prev_year"));
        }
        if (yearDto.isNext()) {
            yearDto.add(linkTo(methodOn(PlaneYearController.class)
                    .getYearByDate(startDate.plusYears(1).getYear())).withRel("next_year"));
        }
        yearDto.add(linkTo(methodOn(PlaneMonthController.class)
                .getMonthByDate(date.getYear(), date.getMonthValue())).withRel("months"));
        return yearDto;
    }

    private YearDto daysToYearDTO(LocalDate date, List<Day> days) {
        int total = 0, planes23 = 0, planes0 = 0, absAltitude = 0, absSpeed = 0, absDaysWithLessThanThirtyPlanes = 0;
        boolean prev = date.minusYears(1).plusDays(1).isAfter(this.date.withMonth(1).withDayOfMonth(1));
        LocalDate endDate = date.plusYears(1).minusDays(1);
        boolean next = endDate.isBefore(LocalDate.now());
        int firstMonth;
        if (date.getYear() == this.date.getYear()) {
            firstMonth = this.date.getMonthValue();
        } else {
            firstMonth = 1;
        }
        int[] months = new int[12];
        for (Day day : days) {
            total += day.getTotal();
            planes23 += day.getPlanes23();
            planes0 += day.getPlanes0();
            absAltitude += day.getAbsAltitude();
            absSpeed += day.getAbsSpeed();
            if (day.isLessThanThirtyPlanes())
                absDaysWithLessThanThirtyPlanes += 1;
            months[day.getDate().getMonthValue() - 1] += day.getTotal();
        }
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
        float percentage = (absDaysWithLessThanThirtyPlanes / (float) days.size()) * 100;
        float daysWithLessThanThirtyPlanes = Math.round(percentage * 100) / 100f;
        int avgAltitude = 0, avgSpeed = 0;
        if (total != 0) {
            avgAltitude = absAltitude / total;
            avgSpeed = absSpeed / total;
        }
        return new YearDto(date, endDate, LocalDateTime.now(), date.getYear(), firstMonth, prev, next,
                total, avgPlanes, planes23, planes0, avgAltitude, avgSpeed, daysWithLessThanThirtyPlanes, months);
    }
}
