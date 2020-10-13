package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.dto.MapEntryDto;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.model.Day;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DaysMapperUtil {

    public static void incrementDepartures(Day day, DeparturesDto departures, Map<String, Integer> airports) {
        departures.setContinental_abs(departures.getContinental_abs() + day.getDeparturesContinentalAbs());
        departures.setInternational_abs(departures.getInternational_abs() + day.getDeparturesInternationalAbs());
        departures.setNational_abs(departures.getNational_abs() + day.getDeparturesNationalAbs());
        departures.setUnknown_abs(departures.getUnknown_abs() + day.getDeparturesUnknownAbs());
        for (Map.Entry<String, Integer> airport : day.getDeparturesTop().entrySet()) {
            if (airports.containsKey(airport.getKey()))
                airports.put(airport.getKey(), airports.get(airport.getKey()) + airport.getValue());
            else
                airports.put(airport.getKey(), airport.getValue());
        }
    }

    public static DeparturesDto setDepartures(DeparturesDto departures) {
        int absDepartures = departures.getContinental_abs() + departures.getInternational_abs() +
                departures.getNational_abs() + departures.getUnknown_abs();
        if (absDepartures > 0) {
            int departuresContinental = Math.round(departures.getContinental_abs() / (float) absDepartures * 100);
            departures.setContinental(departuresContinental);

            int departuresInternational = Math.round(departures.getInternational_abs() / (float) absDepartures * 100);
            departures.setInternational(departuresInternational);

            int departuresNational = Math.round(departures.getNational_abs() / (float) absDepartures * 100);
            departures.setNational(departuresNational);

            int departuresUnknown = 100 - departuresContinental - departuresInternational - departuresNational;
            departures.setUnknown(departuresUnknown);
        }
        return departures;
    }

    @NotNull
    public static List<MapEntryDto> mapToList(Map<String, Integer> departures, int limit) {
        return departures.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(limit)
                .map(e -> new MapEntryDto(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}
