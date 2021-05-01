package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.dto.MapEntryDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.util.PropertiesHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DayMapper {
    private static LocalDate date;

    static {
        try {
            DayMapper.date = LocalDate.parse(PropertiesHandler.getProperties("src/main/resources/start-date.properties").getProperty("startDate"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DayDto toDto(Day day) {
        DayDto dayDto = new DayDto();
        dayDto.setDate(day.getDate());
        dayDto.setNow(LocalDateTime.now());
        dayDto.setWeekday(dayDto.getDate().getDayOfWeek().name());
        dayDto.setPrev(dayDto.getDate().isAfter(DayMapper.date));
        dayDto.setNext(dayDto.getDate().isBefore(LocalDate.now()));
        dayDto.setTotal(day.getTotal());
        dayDto.setAvg_flights(getAvgFlights(day));
        dayDto.setAvg_altitude(day.getAvgAltitude());
        dayDto.setAvg_speed(day.getAvgSpeed());
        dayDto.setLess_than_thirty_flights(day.isLessThanThirtyFlights());
        dayDto.setWind_speed(day.getWindSpeed());
        dayDto.setHours_flight(getHoursFlight(day));
        dayDto.setHours_wind(getHoursWind(day));
        dayDto.setDepartures(getDepartures(day));
        dayDto.setAirports(getAirportDtos(day));
        dayDto.setFlights(!day.getFlights().isEmpty());
        return dayDto;
    }

    private static Integer[] getAvgFlights(Day day) {
        int absFlightsDay = day.getTotal() - day.getFlights23() - day.getFlights0();
        Integer[] avgFlights;
        if (LocalDate.now().isEqual(date)) {
            int currentHour = LocalDateTime.now().getHour();
            avgFlights = new Integer[currentHour + 1];
            if (currentHour <= 6) {
                Arrays.fill(avgFlights, null);
            } else {
                Arrays.fill(avgFlights, 0, 6, null);
                Arrays.fill(avgFlights, 6, avgFlights.length, absFlightsDay / (currentHour - 6));
            }
        } else {
            avgFlights = new Integer[24];
            Arrays.fill(avgFlights, 0, 6, null);
            Arrays.fill(avgFlights, 6, avgFlights.length, absFlightsDay / 17);
        }
        return avgFlights;
    }

    private static int[] getHoursFlight(Day day) {
        int[] hours_flight;
        if (LocalDate.now().isEqual(day.getDate())) {
            int currentHour = LocalDateTime.now().getHour();
            hours_flight = Arrays.copyOfRange(day.getHoursFlight(), 0, currentHour + 1);
        } else {
            hours_flight = day.getHoursFlight();
        }
        return hours_flight;
    }

    private static Integer[] getHoursWind(Day day) {
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

    private static DeparturesDto getDepartures(Day day) {
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

    private static List<MapEntryDto> getAirportDtos(Day day) {
        return day.getDeparturesTop().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> new MapEntryDto(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}
