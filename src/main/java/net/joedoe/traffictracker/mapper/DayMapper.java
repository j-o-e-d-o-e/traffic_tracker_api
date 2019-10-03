package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.PlaneController;
import net.joedoe.traffictracker.controller.PlaneDayController;
import net.joedoe.traffictracker.controller.PlaneWeekController;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.model.Day;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@PropertySource("classpath:start-date.properties")
@Component
public class DayMapper extends ResourceAssemblerSupport<Day, DayDto> {
    @Value("${startDate}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public DayMapper() {
        super(PlaneDayController.class, DayDto.class);
    }

    @Override
    public DayDto toResource(Day day) {
        if (day == null) {
            return null;
        }
        LocalDate date = day.getDate();
        DayDto dayDto = dayToDayDto(day);

        dayDto.add(linkTo(methodOn(PlaneDayController.class)
                .getDayById(day.getId())).withSelfRel());
        if (date.isAfter(this.date)) {
            dayDto.add(linkTo(methodOn(PlaneDayController.class)
                    .getDayByDate(date.minusDays(1))).withRel("prev_day"));
        }
        if (date.isBefore(LocalDate.now())) {
            dayDto.add(linkTo(methodOn(PlaneDayController.class)
                    .getDayByDate(date.plusDays(1))).withRel("next_day"));
        }
        if (day.getTotal() != 0) {
            dayDto.add(linkTo(methodOn(PlaneController.class)
                    .getPlanesByDate(date, null, null)).withRel("planes"));
        }
        dayDto.add(linkTo(methodOn(PlaneWeekController.class)
                .getWeekByDate(date)).withRel("week"));
        return dayDto;
    }

    private DayDto dayToDayDto(Day day) {
        int avgPlanes;
        int absPlanesDay = day.getTotal() - day.getPlanes23() - day.getPlanes0();
        if (LocalDate.now().isEqual(day.getDate())) {
            avgPlanes = absPlanesDay / (LocalDateTime.now().getHour() - 6);
        } else {
            avgPlanes = absPlanesDay / 17;
        }
        return new DayDto(day.getDate(), day.getDate().getDayOfWeek().name(), day.getTotal(), avgPlanes,
                day.getAvgAltitude(), day.getAvgSpeed(), day.getWindSpeed(), day.getHoursPlane(), day.getHoursWind());
    }
}
