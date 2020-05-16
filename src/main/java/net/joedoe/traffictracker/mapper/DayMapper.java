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
import java.util.Arrays;

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
        if (dayDto.isPrev()) {
            dayDto.add(linkTo(methodOn(PlaneDayController.class)
                    .getDayByDate(date.minusDays(1))).withRel("prev_day"));
        }
        if (dayDto.isNext()) {
            dayDto.add(linkTo(methodOn(PlaneDayController.class)
                    .getDayByDate(date.plusDays(1))).withRel("next_day"));
        }
        if (!day.getPlanes().isEmpty()) {
            dayDto.add(linkTo(methodOn(PlaneController.class)
                    .getPlanesByDate(date, null, null)).withRel("planes"));
        }
        dayDto.add(linkTo(methodOn(PlaneWeekController.class)
                .getWeekByDate(date)).withRel("week"));
        return dayDto;
    }

    private DayDto dayToDayDto(Day day) {
        int absPlanesDay = day.getTotal() - day.getPlanes23() - day.getPlanes0();
        LocalDate date = day.getDate();
        boolean prev = date.isAfter(this.date);
        boolean next = date.isBefore(LocalDate.now());
        Integer[] avgPlanes;
        Integer[] hours_wind;
        int[] hours_plane;
        if (LocalDate.now().isEqual(date)) {
            int currentHour = LocalDateTime.now().getHour();
            avgPlanes = new Integer[currentHour + 1];
            hours_wind = new Integer[currentHour + 1];
            if (currentHour <= 6) {
                Arrays.fill(avgPlanes, null);
                Arrays.fill(hours_wind, null);
            } else {
                Arrays.fill(avgPlanes, 0, 6, null);
                Arrays.fill(avgPlanes, 6, avgPlanes.length, absPlanesDay / (currentHour - 6));
                Arrays.fill(hours_wind, 0, 6, null);
                for (int i = 6; i <= currentHour; i++){
                    hours_wind[i] = day.getHoursWind()[i];
                }
            }
            hours_plane = Arrays.copyOfRange(day.getHoursPlane(), 0, currentHour + 1);
        } else {
            avgPlanes = new Integer[24];
            Arrays.fill(avgPlanes, 0, 6, null);
            Arrays.fill(avgPlanes, 6, avgPlanes.length, absPlanesDay / 17);
            hours_wind = new Integer[24];
            Arrays.fill(hours_wind, 0, 6, null);
            for (int i = 6; i < hours_wind.length; i++){
                hours_wind[i] = day.getHoursWind()[i];
            }
            hours_plane = day.getHoursPlane();
        }
        return new DayDto(date, LocalDateTime.now(), date.getDayOfWeek().name(), prev, next,
                day.getTotal(), avgPlanes, day.getAvgAltitude(), day.getAvgSpeed(),
                day.isLessThanThirtyPlanes(), day.getWindSpeed(), hours_plane, hours_wind
        );
    }
}
