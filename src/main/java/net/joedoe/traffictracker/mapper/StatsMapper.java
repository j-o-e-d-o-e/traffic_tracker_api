package net.joedoe.traffictracker.mapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.dto.StatsDto.StatsDay;
import net.joedoe.traffictracker.dto.StatsDto.StatsPlane;
import net.joedoe.traffictracker.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Data
public class StatsMapper {

    public static StatsDto toStatsDto(List<Day> days, ForecastScore score) {
        StatsDto statsDto = iterateDays(days);
        statsDto.setScore(ForecastScoreMapper.toResource(score));
        return statsDto;
    }

    public static StatsDto iterateDays(List<Day> days) {
        int total = 0, mostFlightsWithinOneHour = 0;
        Day dayWithMostFlights = new Day(), dayWithMostFlightsWithinOneHour = new Day();
        DeparturesDto departuresDto = new DeparturesDto();
        Map<String, Integer> airports = new HashMap<>();
        Map<String, Integer> planes = new HashMap<>();
        int daysWithLessThanThirtyFlights = 0, hoursWithNoFlights = 0;
        StatsPlane planeWithMostFlightsWithinOneDay = new StatsPlane();
        Flight maxAltitude = null, minAltitude = null, maxSpeed = null, minSpeed = null;
        Map<String, Integer> airlines = new HashMap<>();
        for (Day day : days) {
            // total
            total += day.getTotal();
            // day_with_most_flights
            if (day.getTotal() >= dayWithMostFlights.getTotal())
                dayWithMostFlights = day;
            // day_with_most_flights_within_one_hour
            int tmp = Arrays.stream(day.getHoursFlight()).max().orElse(0);
            if (tmp >= mostFlightsWithinOneHour) {
                dayWithMostFlightsWithinOneHour = day;
                mostFlightsWithinOneHour = tmp;
            }
            if (day.getDate().isBefore(LocalDate.now())) {
                // days with less than 30 flights
                if (day.isLessThanThirtyFlights()) daysWithLessThanThirtyFlights++;
                // hours with no flights
                hoursWithNoFlights += Arrays.stream(Arrays.copyOfRange(day.getHoursFlight(), 6, 23)).filter(h -> h == 0).count();
            }
            // departures
            if (day.getDeparturesTop().size() > 0)
                DaysMapperUtil.incrementDepartures(day, departuresDto, airports);
            Map<String, Integer> planesDaily = new HashMap<>();
            for (Flight flight : day.getFlights()) {
                Plane plane = flight.getPlane();
                String icao = plane.getIcao();
                // plane_with_most_flights
                if (planes.containsKey(icao))
                    planes.put(icao, planes.get(icao) + 1);
                else
                    planes.put(icao, 1);
                // plane_with_most_flights_within_one_day
                if (planesDaily.containsKey(icao))
                    planesDaily.put(icao, planesDaily.get(icao) + 1);
                else
                    planesDaily.put(icao, 1);
                // max_altitude
                if (maxAltitude == null || flight.getAltitude() >= maxAltitude.getAltitude())
                    maxAltitude = flight;
                    // min_altitude
                else if (minAltitude == null || flight.getAltitude() <= minAltitude.getAltitude())
                    minAltitude = flight;
                // max_speed
                if (maxSpeed == null || flight.getSpeed() >= maxSpeed.getSpeed())
                    maxSpeed = flight;
                    // min_speed
                else if (minSpeed == null || flight.getSpeed() <= minSpeed.getSpeed())
                    minSpeed = flight;
                // airlines
                Airline airline = flight.getAirline();
                String airlineName = airline.getName() == null ? airline.getIcao() : airline.getName();
                if (airlineName == null) continue;
                if (airlines.containsKey(airlineName))
                    airlines.put(airlineName, airlines.get(airlineName) + 1);
                else
                    airlines.put(airlineName, 1);
            }
//             plane_with_most_flights_within_one_day
            Map.Entry<String, Integer> plane = planesDaily.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            if (plane != null && plane.getValue() >= planeWithMostFlightsWithinOneDay.getStats()) {
                planeWithMostFlightsWithinOneDay.setDay(DayMapper.toDto(day, true, true));
                planeWithMostFlightsWithinOneDay.setStats(plane.getValue());
                planeWithMostFlightsWithinOneDay.setIcao(plane.getKey());
            }
        }
        StatsDto statsDto = new StatsDto();
        statsDto.setTotal(total);
        StatsDay statsDay = new StatsDay(DayMapper.toDto(dayWithMostFlights, true, true), dayWithMostFlights.getTotal());
        statsDto.setDay_with_most_flights(statsDay);
        statsDay = new StatsDay(DayMapper.toDto(dayWithMostFlightsWithinOneHour, true, true), mostFlightsWithinOneHour);
        statsDto.setDay_with_most_flights_within_one_hour(statsDay);
        float percentage = (daysWithLessThanThirtyFlights / (float) (days.size() - 1)) * 100;
        statsDto.setDays_with_less_than_thirty_flights(Math.round(percentage * 100) / 100f);
        percentage = (hoursWithNoFlights / ((float) 17 * (days.size() - 1))) * 100;
        statsDto.setHours_with_no_flights(Math.round(percentage * 100) / 100f);
        statsDto.setDepartures(DaysMapperUtil.setDepartures(departuresDto));
        statsDto.setAirports(DaysMapperUtil.mapToList(airports, 10));
        statsDto.setPlane_with_most_flights(getPlaneWithMostFlights(planes));
        statsDto.setPlane_with_most_flights_within_one_day(planeWithMostFlightsWithinOneDay);
        assert maxAltitude != null;
        statsDto.setMax_altitude(FlightMapper.toDto(maxAltitude));
        statsDto.setMin_altitude(FlightMapper.toDto(minAltitude));
        statsDto.setMax_speed(FlightMapper.toDto(maxSpeed));
        statsDto.setMin_speed(FlightMapper.toDto(minSpeed));
        statsDto.setAirlines(DaysMapperUtil.mapToList(airlines, 10));
        return statsDto;
    }

    private static StatsPlane getPlaneWithMostFlights(Map<String, Integer> flights) {
        Map.Entry<String, Integer> entry = flights.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
        if (entry == null) return null;
        StatsPlane plane = new StatsPlane();
        plane.setIcao(entry.getKey());
        plane.setStats(entry.getValue());
        return plane;
    }
}
