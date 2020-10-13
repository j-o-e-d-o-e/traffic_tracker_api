package net.joedoe.traffictracker.mapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.dto.StatsDto.StatsDay;
import net.joedoe.traffictracker.dto.StatsDto.StatsPlane;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.ForecastScore;
import net.joedoe.traffictracker.model.Plane;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@Data
public class StatsMapper {
    private final DayMapper dayMapper;
    private final PlaneMapper planeMapper;

    public StatsMapper(DayMapper dayMapper, PlaneMapper planeMapper) {
        this.dayMapper = dayMapper;
        this.planeMapper = planeMapper;
    }

    public StatsDto toStatsDto(List<Day> days, ForecastScore score) {
        StatsDto statsDto = iterateDays(days);
        statsDto.setScore(ForecastScoreMapper.toResource(score));
        return statsDto;
    }

    public StatsDto iterateDays(List<Day> days) {
        int total = 0, mostFlightsWithinOneHour = 0;
        Day dayWithMostFlights = new Day(), dayWithMostFlightsWithinOneHour = new Day();
        DeparturesDto departuresDto = new DeparturesDto();
        Map<String, Integer> airports = new HashMap<>();
        Map<String, Integer> flights = new HashMap<>();
        StatsPlane planeWithMostFlightsWithinOneDay = new StatsPlane();
        Plane maxAltitude = null, minAltitude = null, maxSpeed = null, minSpeed = null;
        Map<String, Integer> airlines = new HashMap<>();
        for (Day day : days) {
            // total
            total += day.getTotal();
            // day_with_most_flights
            if (day.getTotal() >= dayWithMostFlights.getTotal())
                dayWithMostFlights = day;
            // day_with_most_flights_within_one_hour
            int tmp = Arrays.stream(day.getHoursPlane()).max().orElse(0);
            if (tmp >= mostFlightsWithinOneHour) {
                dayWithMostFlightsWithinOneHour = day;
                mostFlightsWithinOneHour = tmp;
            }
            if (day.getDeparturesTop().isEmpty()) continue;
            // departures
            DaysMapperUtil.incrementDepartures(day, departuresDto, airports);
            if (day.getPlanes() == null) continue;
            Map<String, Integer> flightsDaily = new HashMap<>();
            for (Plane plane : day.getPlanes()) {
                // plane_with_most_flights
                if (flights.containsKey(plane.getIcao()))
                    flights.put(plane.getIcao(), flights.get(plane.getIcao()) + 1);
                else
                    flights.put(plane.getIcao(), 1);
                // plane_with_most_flights_within_one_day
                if (flightsDaily.containsKey(plane.getIcao()))
                    flightsDaily.put(plane.getIcao(), flightsDaily.get(plane.getIcao()) + 1);
                else
                    flightsDaily.put(plane.getIcao(), 1);
                // max_altitude
                if (maxAltitude == null || plane.getAltitude() >= maxAltitude.getAltitude())
                    maxAltitude = plane;
                    // min_altitude
                else if (minAltitude == null || plane.getAltitude() <= minAltitude.getAltitude())
                    minAltitude = plane;
                // max_speed
                if (maxSpeed == null || plane.getAltitude() >= maxSpeed.getAltitude())
                    maxSpeed = plane;
                    // min_speed
                else if (minSpeed == null || plane.getAltitude() <= minSpeed.getAltitude())
                    minSpeed = plane;
                // airlines
                String airline = plane.getAirlineName() == null ? plane.getAirline() : plane.getAirlineName();
                if (airline == null) continue;
                if (airlines.containsKey(airline))
                    airlines.put(airline, airlines.get(airline) + 1);
                else
                    airlines.put(airline, 1);
            }
            // plane_with_most_flights_within_one_day
            Map.Entry<String, Integer> plane = flightsDaily.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            if (plane != null && plane.getValue() >= planeWithMostFlightsWithinOneDay.getStats()) {
                planeWithMostFlightsWithinOneDay.setDay(dayMapper.toResource(day));
                planeWithMostFlightsWithinOneDay.setStats(plane.getValue());
                planeWithMostFlightsWithinOneDay.setIcao(plane.getKey());
            }
        }
        StatsDto statsDto = new StatsDto();
        statsDto.setTotal(total);
        StatsDay statsDay = new StatsDay(dayMapper.toResource(dayWithMostFlights), dayWithMostFlights.getTotal());
        statsDto.setDay_with_most_flights(statsDay);
        statsDay = new StatsDay(dayMapper.toResource(dayWithMostFlightsWithinOneHour), mostFlightsWithinOneHour);
        statsDto.setDay_with_most_flights_within_one_hour(statsDay);
        statsDto.setDepartures(DaysMapperUtil.setDepartures(departuresDto));
        statsDto.setAirports(DaysMapperUtil.mapToList(airports, 10));
        statsDto.setPlane_with_most_flights(getPlaneWithMostFlights(flights));
        statsDto.setPlane_with_most_flights_within_one_day(planeWithMostFlightsWithinOneDay);
        statsDto.setMax_altitude(planeMapper.toResource(maxAltitude));
        statsDto.setMin_altitude(planeMapper.toResource(minAltitude));
        statsDto.setMax_speed(planeMapper.toResource(maxSpeed));
        statsDto.setMin_speed(planeMapper.toResource(minSpeed));
        statsDto.setAirlines(DaysMapperUtil.mapToList(airlines, 10));
        return statsDto;
    }

    private StatsPlane getPlaneWithMostFlights(Map<String, Integer> flights) {
        Map.Entry<String, Integer> entry = flights.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
        if (entry == null) return null;
        StatsPlane plane = new StatsPlane();
        plane.setIcao(entry.getKey());
        plane.setStats(entry.getValue());
        return plane;
    }
}
