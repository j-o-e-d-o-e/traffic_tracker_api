package net.joedoe.traffictracker.client;

import net.joedoe.traffictracker.service.DayService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WeatherClientTest {
    @Mock
    private DayService service;
    private WeatherClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        client = new WeatherClient(service);
    }

    @Ignore
    @Test
    public void fetchWeather() {
        client.fetchWeather();
    }
}