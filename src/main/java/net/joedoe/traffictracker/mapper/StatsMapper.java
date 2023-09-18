package net.joedoe.traffictracker.mapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DeparturesDto;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.dto.StatsDto.StatsDay;
import net.joedoe.traffictracker.dto.StatsDto.StatsPlane;
import net.joedoe.traffictracker.model.ForecastScore;
import net.joedoe.traffictracker.model.Stats;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
public class StatsMapper {
    public static StatsDto toStatsDto(Stats stats, ForecastScore score) {
        StatsDto statsDto = new StatsDto();
        statsDto.setDays_total(stats.getDaysTotal());
        statsDto.setFlights_total(stats.getFlightsTotal());
        Stats.StatsDay dayWithMostFlights = stats.getDayWithMostFlights();
        statsDto.setDay_with_most_flights(new StatsDay(dayWithMostFlights.getDate(),
                dayWithMostFlights.getStats()));
        Stats.StatsDay dayWithMostFlightsWithinOneHour = stats.getDayWithMostFlightsWithinOneHour();
        statsDto.setDay_with_most_flights_within_one_hour(new StatsDay(dayWithMostFlightsWithinOneHour.getDate(),
                dayWithMostFlightsWithinOneHour.getStats()));
        statsDto.setDays_with_less_than_thirty_flights(stats.getDaysWithLessThanThirtyFlights());
        statsDto.setHours_with_no_flights(stats.getHoursWithNoFlights());
        statsDto.setDepartures(toDeparturesDto(stats));
        statsDto.setAirports(DaysMapperUtil.mapToList(stats.getAirports(), 10));
        Stats.StatsPlane planeWithMostFlights = stats.getPlaneWithMostFlights();
        statsDto.setPlane_with_most_flights(new StatsPlane(null, planeWithMostFlights.getIcao(),
                planeWithMostFlights.getStats()));
        Stats.StatsPlane planeWithMostFlightsWithinOneDay = stats.getPlaneWithMostFlightsWithinOneDay();
        statsDto.setPlane_with_most_flights_within_one_day(new StatsPlane(planeWithMostFlightsWithinOneDay.getDate(),
                planeWithMostFlightsWithinOneDay.getIcao(), planeWithMostFlightsWithinOneDay.getStats()));
        Stats.StatsPlane flightWithMaxAltitude = stats.getFlightWithMaxAltitude();
        statsDto.setMax_altitude(new StatsPlane(flightWithMaxAltitude.getDate(),
                flightWithMaxAltitude.getIcao(), flightWithMaxAltitude.getStats()));
        Stats.StatsPlane flightWithMinAltitude = stats.getFlightWithMinAltitude();
        statsDto.setMin_altitude(new StatsPlane(flightWithMinAltitude.getDate(),
                flightWithMinAltitude.getIcao(), flightWithMinAltitude.getStats()));
        Stats.StatsPlane flightWithMaxSpeed = stats.getFlightWithMaxSpeed();
        statsDto.setMax_speed(new StatsPlane(flightWithMaxSpeed.getDate(),
                flightWithMaxSpeed.getIcao(), flightWithMaxSpeed.getStats()));
        Stats.StatsPlane flightWithMinSpeed = stats.getFlightWithMinSpeed();
        statsDto.setMin_speed(new StatsPlane(flightWithMinSpeed.getDate(),
                flightWithMinSpeed.getIcao(), flightWithMinSpeed.getStats()));
        statsDto.setAirlines(DaysMapperUtil.mapToList(stats.getAirlines(), 10));
        statsDto.setScore(ForecastScoreMapper.toResource(score));
        return statsDto;
    }

    @NotNull
    private static DeparturesDto toDeparturesDto(Stats stats) {
        DeparturesDto departuresDto = new DeparturesDto();
        departuresDto.setContinental_abs(stats.getDeparturesContinentalAbs());
        departuresDto.setContinental(stats.getDeparturesContinental());
        departuresDto.setInternational_abs(stats.getDeparturesInternationalAbs());
        departuresDto.setInternational(stats.getDeparturesInternational());
        departuresDto.setNational_abs(stats.getDeparturesNationalAbs());
        departuresDto.setNational(stats.getDeparturesNational());
        departuresDto.setUnknown_abs(stats.getDeparturesUnknownAbs());
        departuresDto.setUnknown(stats.getDeparturesUnknown());
        return departuresDto;
    }
}
