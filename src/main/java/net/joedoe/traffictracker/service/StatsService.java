package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.mapper.StatsMapper;
import net.joedoe.traffictracker.model.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

@Service
public class StatsService {
    private final DayService dayService;
    Collector<Day, Stats, StatsDto> collector;

    public StatsService(DayService dayService, ForecastService service) {
        this.dayService = dayService;
        this.collector = Collector.of(StatsService.supplier(), StatsService.accumulator(),
                StatsService.combiner(), StatsService.finisher(service));
    }

    public StatsDto getStats() {
        return dayService.findAllJoinFetchFlights().stream().parallel().collect(collector);
    }

    private static Supplier<Stats> supplier() {
        return Stats::new;
    }

    private static BiConsumer<Stats, Day> accumulator() {
        return (st, day) -> {
            st.setDaysTotal(st.getDaysTotal() + 1);
            // total flights
            st.setFlightsTotal(st.getFlightsTotal() + day.getTotal());
            // day_with_most_flights
            Stats.StatsDay dayWithMostFlights = st.getDayWithMostFlights();
            if (day.getTotal() >= dayWithMostFlights.getStats()) {
                dayWithMostFlights.setDate(day.getDate());
                dayWithMostFlights.setStats(day.getTotal());
            }
            // day_with_most_flights_within_one_hour
            Stats.StatsDay dayWithMostFlightsWithinOneHour = st.getDayWithMostFlightsWithinOneHour();
            int max = Arrays.stream(day.getHoursFlight(), 6, 23).max().orElse(0);
            if (max > dayWithMostFlightsWithinOneHour.getStats()) {
                dayWithMostFlightsWithinOneHour.setDate(day.getDate());
                dayWithMostFlightsWithinOneHour.setStats(max);
            }
            // days with less than 30 flights: 1st step
            if (day.isLessThanThirtyFlights())
                st.setDaysWithLessThanThirtyFlightsAbs(st.getDaysWithLessThanThirtyFlightsAbs() + 1);
            // hours with no flights: 1st step
            int hoursWithNoFlights = (int) Arrays.stream(day.getHoursFlight(), 6, 23).filter(h -> h == 0).count();
            st.setHoursWithNoFlightsAbs(st.getHoursWithNoFlightsAbs() + hoursWithNoFlights);
            // departures: 1st step
            if (!day.getDeparturesTop().isEmpty()) {
                setDeparturesAbs(st, day.getDeparturesContinentalAbs(), day.getDeparturesInternationalAbs(), day.getDeparturesNationalAbs(), day.getDeparturesUnknownAbs());
                Map<String, Integer> airports = st.getAirports();
                for (Map.Entry<String, Integer> airport : day.getDeparturesTop().entrySet()) {
                    if (airports.containsKey(airport.getKey()))
                        airports.put(airport.getKey(), airports.get(airport.getKey()) + airport.getValue());
                    else airports.put(airport.getKey(), airport.getValue());
                }
            }
            Map<String, Integer> planesDaily = new HashMap<>();
            for (Flight flight : day.getFlights()) {
                String icao = flight.getPlane().getIcao();
                // max_altitude, min_altitude
                Stats.StatsPlane flightWithMaxAltitude = st.getFlightWithMaxAltitude();
                Stats.StatsPlane flightWithMinAltitude = st.getFlightWithMinAltitude();
                if (flight.getAltitude() >= flightWithMaxAltitude.getStats()) {
                    flightWithMaxAltitude.setDate(day.getDate());
                    flightWithMaxAltitude.setIcao(icao);
                    flightWithMaxAltitude.setStats(flight.getAltitude());
                } else if (flightWithMinAltitude.getStats() == 0 || flight.getAltitude() <= flightWithMinAltitude.getStats()) {
                    flightWithMinAltitude.setDate(day.getDate());
                    flightWithMinAltitude.setIcao(icao);
                    flightWithMinAltitude.setStats(flight.getAltitude());
                }
                // max_speed, min_speed
                Stats.StatsPlane flightWithMaxSpeed = st.getFlightWithMaxSpeed();
                Stats.StatsPlane flightWithMinSpeed = st.getFlightWithMinSpeed();
                if (flight.getSpeed() >= flightWithMaxSpeed.getStats()) {
                    flightWithMaxSpeed.setDate(day.getDate());
                    flightWithMaxSpeed.setIcao(icao);
                    flightWithMaxSpeed.setStats(flight.getSpeed());
                } else if (flightWithMinSpeed.getStats() == 0 || flight.getSpeed() <= flightWithMinSpeed.getStats()) {
                    flightWithMinSpeed.setDate(day.getDate());
                    flightWithMinSpeed.setIcao(icao);
                    flightWithMinSpeed.setStats(flight.getSpeed());
                }
                // airlines
                Airline airline = flight.getAirline();
                String airlineName = airline.getName() == null ? airline.getIcao() : airline.getName();
                if (airlineName == null) continue;
                Map<String, Integer> airlines = st.getAirlines();
                if (airlines.containsKey(airlineName)) airlines.put(airlineName, airlines.get(airlineName) + 1);
                else airlines.put(airlineName, 1);

                // plane_with_most_flights: 1st step
                Map<String, Integer> planes = st.getPlanes();
                if (planes.containsKey(icao)) planes.put(icao, planes.get(icao) + 1);
                else planes.put(icao, 1);
                // plane_with_most_flights_within_one_day: 1st step
                if (planesDaily.containsKey(icao)) planesDaily.put(icao, planesDaily.get(icao) + 1);
                else planesDaily.put(icao, 1);
            }
            // plane_with_most_flights_within_one_day: 2nd step
            Map.Entry<String, Integer> mostFlightsWithinOneDay = planesDaily.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            if (mostFlightsWithinOneDay == null) return;
            Stats.StatsPlane planeWithMostFlightsWithinOneDay = st.getPlaneWithMostFlightsWithinOneDay();
            if (mostFlightsWithinOneDay.getValue() >= planeWithMostFlightsWithinOneDay.getStats()) {
                planeWithMostFlightsWithinOneDay.setDate(day.getDate());
                planeWithMostFlightsWithinOneDay.setIcao(mostFlightsWithinOneDay.getKey());
                planeWithMostFlightsWithinOneDay.setStats(mostFlightsWithinOneDay.getValue());
            }
        };
    }

    private static BinaryOperator<Stats> combiner() {
        return (st1, st2) -> {
            st1.setDaysTotal(st1.getDaysTotal() + st2.getDaysTotal());
            st1.setFlightsTotal(st1.getFlightsTotal() + st2.getFlightsTotal());
            if (st1.getDayWithMostFlights().compareTo(st2.getDayWithMostFlights()) < 0)
                st1.setDayWithMostFlights(st2.getDayWithMostFlights());
            if (st1.getDayWithMostFlightsWithinOneHour().compareTo(st2.getDayWithMostFlightsWithinOneHour()) < 0)
                st1.setDayWithMostFlightsWithinOneHour(st2.getDayWithMostFlightsWithinOneHour());
            st1.setDaysWithLessThanThirtyFlightsAbs(st1.getDaysWithLessThanThirtyFlightsAbs() + st2.getDaysWithLessThanThirtyFlightsAbs());
            st1.setHoursWithNoFlightsAbs(st1.getHoursWithNoFlightsAbs() + st2.getHoursWithNoFlightsAbs());
            setDeparturesAbs(st1, st2.getDeparturesContinentalAbs(), st2.getDeparturesInternationalAbs(), st2.getDeparturesNationalAbs(), st2.getDeparturesUnknownAbs());
            Map<String, Integer> airports = st1.getAirports();
            for (Map.Entry<String, Integer> entry : st2.getAirports().entrySet())
                airports.merge(entry.getKey(), entry.getValue(), Integer::sum);
            if (st1.getFlightWithMaxAltitude().compareTo(st2.getFlightWithMaxAltitude()) < 0)
                st1.setFlightWithMaxAltitude(st2.getFlightWithMaxAltitude());
            if (st2.getFlightWithMinAltitude().getStats() != 0 &&
                    (st1.getFlightWithMinAltitude().getStats() == 0 || st1.getFlightWithMinAltitude().compareTo(st2.getFlightWithMinAltitude()) > 0))
                st1.setFlightWithMinAltitude(st2.getFlightWithMinAltitude());
            if (st1.getFlightWithMaxSpeed().compareTo(st2.getFlightWithMaxSpeed()) < 0)
                st1.setFlightWithMaxSpeed(st2.getFlightWithMaxSpeed());
            if (st2.getFlightWithMinSpeed().getStats() != 0 &&
                    (st1.getFlightWithMinSpeed().getStats() == 0 || st1.getFlightWithMinSpeed().compareTo(st2.getFlightWithMinSpeed()) > 0))
                st1.setFlightWithMinSpeed(st2.getFlightWithMinSpeed());
            Map<String, Integer> airlines = st1.getAirlines();
            for (Map.Entry<String, Integer> entry : st2.getAirlines().entrySet())
                airlines.merge(entry.getKey(), entry.getValue(), Integer::sum);
            Map<String, Integer> planes = st1.getPlanes();
            for (Map.Entry<String, Integer> entry : st2.getPlanes().entrySet())
                planes.merge(entry.getKey(), entry.getValue(), Integer::sum);
            if (st1.getPlaneWithMostFlightsWithinOneDay().compareTo(st2.getPlaneWithMostFlightsWithinOneDay()) < 0)
                st1.setPlaneWithMostFlightsWithinOneDay(st2.getPlaneWithMostFlightsWithinOneDay());
            return st1;
        };
    }

    private static Function<Stats, StatsDto> finisher(ForecastService service) {
        return stats -> {
            // days with less than 30 flights: 2nd step
            float daysWithLessThanThirtyFlights = (stats.getDaysWithLessThanThirtyFlightsAbs() / (float) stats.getDaysTotal()) * 100;
            stats.setDaysWithLessThanThirtyFlights(Math.round(daysWithLessThanThirtyFlights * 100) / 100f);
            // hours with no flights: 2nd step
            float hoursWithNoFlights = (stats.getHoursWithNoFlightsAbs() / ((float) 17 * stats.getDaysTotal())) * 100;
            stats.setHoursWithNoFlights(Math.round(hoursWithNoFlights * 100) / 100f);
            // departures: 2nd step
            int departuresAbs = stats.getDeparturesContinentalAbs() + stats.getDeparturesInternationalAbs()
                    + stats.getDeparturesNationalAbs() + stats.getDeparturesUnknownAbs();
            int departuresContinental = Math.round(stats.getDeparturesContinentalAbs() / (float) departuresAbs * 100);
            stats.setDeparturesContinental(departuresContinental);
            int departuresInternational = Math.round((stats.getDeparturesInternationalAbs() / (float) departuresAbs) * 100);
            stats.setDeparturesInternational(departuresInternational);
            int departuresNational = Math.round((stats.getDeparturesNationalAbs() / (float) departuresAbs) * 100);
            stats.setDeparturesNational(departuresNational);
            int departuresUnknown = 100 - departuresContinental - departuresInternational - departuresNational;
            stats.setDeparturesUnknown(departuresUnknown);
            // plane_with_most_flights: 2nd step
            Stats.StatsPlane planeWithMostFlights = stats.getPlaneWithMostFlights();
            Map.Entry<String, Integer> mostFlights = stats.getPlanes().entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            assert mostFlights != null;
            planeWithMostFlights.setIcao(mostFlights.getKey());
            planeWithMostFlights.setStats(mostFlights.getValue());
            // forecast score
            ForecastScore forecastScore = service.getScore();
            return StatsMapper.toStatsDto(stats, forecastScore);
        };
    }

    private static void setDeparturesAbs(Stats st, Integer departuresContinentalAbs, Integer departuresInternationalAbs, Integer departuresNationalAbs, Integer departuresUnknownAbs) {
        st.setDeparturesContinentalAbs(st.getDeparturesContinentalAbs() + departuresContinentalAbs);
        st.setDeparturesInternationalAbs(st.getDeparturesInternationalAbs() + departuresInternationalAbs);
        st.setDeparturesNationalAbs(st.getDeparturesNationalAbs() + departuresNationalAbs);
        st.setDeparturesUnknownAbs(st.getDeparturesUnknownAbs() + departuresUnknownAbs);
    }
}

