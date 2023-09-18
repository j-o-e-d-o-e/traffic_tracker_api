package net.joedoe.traffictracker.client;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.ForecastsInitTest;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastScore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@Disabled("To avoid remote calls to weatherbit-api and my django-api")
@SpringBootTest
public class ForecastClientTest {
    @Autowired
    private ForecastClient client;

    @Test
    public void fetchForecasts() {
        List<ForecastDay> forecasts = client.fetchForecasts();
        forecasts.forEach(f -> log.info(f.toString()));
    }
    @Test
    public void fetchScores() {
        ForecastScore score = client.fetchScores(ForecastsInitTest.createIntFlights(), ForecastsInitTest.createIntWinds());
        log.info(score.toString());
    }
}
