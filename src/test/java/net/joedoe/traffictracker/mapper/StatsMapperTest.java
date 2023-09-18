package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.StatsDto;
import net.joedoe.traffictracker.model.ForecastScore;
import net.joedoe.traffictracker.model.Stats;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
public class StatsMapperTest {

    @Test
    public void toDto() {
        Stats stats = new Stats();
        Stats.StatsDay statsDay = new Stats.StatsDay(LocalDate.now(), 3);
        Stats.StatsPlane statsPlane = new Stats.StatsPlane(LocalDate.now(), "", 7);
        stats.setDayWithMostFlights(statsDay);
        stats.setDayWithMostFlightsWithinOneHour(statsDay);
        stats.setFlightWithMaxAltitude(statsPlane);
        stats.setFlightWithMinAltitude(statsPlane);
        stats.setFlightWithMaxSpeed(statsPlane);
        stats.setFlightWithMinSpeed(statsPlane);
        stats.setPlaneWithMostFlights(statsPlane);
        stats.setPlaneWithMostFlightsWithinOneDay(statsPlane);
        ForecastScore forecastScore = new ForecastScore();
        forecastScore.setPrecision(1f);
        forecastScore.setMeanAbsoluteError(2f);
        forecastScore.setConfusionMatrix(new int[][]{{1, 2}, {3, 4}});

        StatsDto act = StatsMapper.toStatsDto(stats, forecastScore);

        assertEquals(0, act.getDays_total());
    }
}
