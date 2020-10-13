package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.PlaneController;
import net.joedoe.traffictracker.controller.DayController;
import net.joedoe.traffictracker.controller.WeekController;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.dto.MapEntryDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.utils.PropertiesHandler;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@Component
public class DayMapper extends ResourceAssemblerSupport<Day, DayDto> {
    private LocalDate date;

    public DayMapper() {
        super(DayController.class, DayDto.class);
        try {
            this.date = LocalDate.parse(PropertiesHandler.getProperties("src/main/resources/start-date.properties").getProperty("startDate"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DayDto toResource(Day day) {
        if (day == null) return null;
        LocalDate date = day.getDate();
        DayDto dayDto = dayToDayDto(day);

        dayDto.add(linkTo(methodOn(DayController.class).getDayById(day.getId())).withSelfRel());
        if (dayDto.isPrev())
            dayDto.add(linkTo(methodOn(DayController.class).getDayByDate(date.minusDays(1))).withRel("prev_day"));
        if (dayDto.isNext())
            dayDto.add(linkTo(methodOn(DayController.class).getDayByDate(date.plusDays(1))).withRel("next_day"));
        if (!day.getPlanes().isEmpty())
            dayDto.add(linkTo(methodOn(PlaneController.class).getPlanesByDate(date, null, null)).withRel("planes"));
        dayDto.add(linkTo(methodOn(WeekController.class).getWeekByDate(date)).withRel("week"));
        return dayDto;
    }

    public DayDto dayToDayDto(Day day) {
        DayDto dayDto = new DayDto();
        dayDto.setDate(day.getDate());
        dayDto.setNow(LocalDateTime.now());
        dayDto.setWeekday(dayDto.getDate().getDayOfWeek().name());
        dayDto.setPrev(dayDto.getDate().isAfter(this.date));
        dayDto.setNext(dayDto.getDate().isBefore(LocalDate.now()));
        dayDto.setTotal(day.getTotal());
        dayDto.setAvg_planes(getAvgPlanes(day));
        dayDto.setAvg_altitude(day.getAvgAltitude());
        dayDto.setAvg_speed(day.getAvgSpeed());
        dayDto.setLess_than_thirty_planes(day.isLessThanThirtyPlanes());
        dayDto.setWind_speed(day.getWindSpeed());
        dayDto.setHours_plane(getHoursPlane(day));
        dayDto.setHours_wind(getHoursWind(day));
        dayDto.setDepartures(getDepartures(day));
        dayDto.setAirports(getAirportDtos(day));
        return dayDto;
    }

    private Integer[] getAvgPlanes(Day day) {
        int absPlanesDay = day.getTotal() - day.getPlanes23() - day.getPlanes0();
        Integer[] avgPlanes;
        if (LocalDate.now().isEqual(date)) {
            int currentHour = LocalDateTime.now().getHour();
            avgPlanes = new Integer[currentHour + 1];
            if (currentHour <= 6) {
                Arrays.fill(avgPlanes, null);
            } else {
                Arrays.fill(avgPlanes, 0, 6, null);
                Arrays.fill(avgPlanes, 6, avgPlanes.length, absPlanesDay / (currentHour - 6));
            }
        } else {
            avgPlanes = new Integer[24];
            Arrays.fill(avgPlanes, 0, 6, null);
            Arrays.fill(avgPlanes, 6, avgPlanes.length, absPlanesDay / 17);
        }
        return avgPlanes;
    }

    private int[] getHoursPlane(Day day) {
        int[] hours_plane;
        if (LocalDate.now().isEqual(day.getDate())) {
            int currentHour = LocalDateTime.now().getHour();
            hours_plane = Arrays.copyOfRange(day.getHoursPlane(), 0, currentHour + 1);
        } else {
            hours_plane = day.getHoursPlane();
        }
        return hours_plane;
    }

    private Integer[] getHoursWind(Day day) {
        Integer[] hoursWind;
        if (LocalDate.now().isEqual(day.getDate())) {
            int currentHour = LocalDateTime.now().getHour();
            hoursWind = new Integer[currentHour + 1];
            if (currentHour <= 6) {
                Arrays.fill(hoursWind, null);
            } else {
                Arrays.fill(hoursWind, 0, 6, null);
                for (int i = 6; i <= currentHour; i++) {
                    hoursWind[i] = day.getHoursWind()[i];
                }
            }
        } else {
            hoursWind = new Integer[24];
            Arrays.fill(hoursWind, 0, 6, null);
            for (int i = 6; i < hoursWind.length; i++) {
                hoursWind[i] = day.getHoursWind()[i];
            }
        }
        return hoursWind;
    }

    private DeparturesDto getDepartures(Day day) {
        DeparturesDto departures = new DeparturesDto();
        if (day.getDeparturesContinentalAbs() != null)
            departures.setContinental_abs(day.getDeparturesContinentalAbs());
        if (day.getDeparturesContinental() != null)
            departures.setContinental(Math.round(day.getDeparturesContinental() * 100));
        if (day.getDeparturesInternationalAbs() != null)
            departures.setInternational_abs(day.getDeparturesInternationalAbs());
        if (day.getDeparturesInternational() != null)
            departures.setInternational(Math.round(day.getDeparturesInternational() * 100));
        if (day.getDeparturesNationalAbs() != null)
            departures.setNational_abs(day.getDeparturesNationalAbs());
        if (day.getDeparturesNational() != null)
            departures.setNational(Math.round(day.getDeparturesNational() * 100));
        if (day.getDeparturesUnknownAbs() != null)
            departures.setUnknown_abs(day.getDeparturesUnknownAbs());
        if (day.getDeparturesUnknown() != null)
            departures.setUnknown(Math.round(day.getDeparturesUnknown() * 100));
        return departures;
    }

    @NotNull
    private List<MapEntryDto> getAirportDtos(Day day) {
        return day.getDeparturesTop().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> new MapEntryDto(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}
