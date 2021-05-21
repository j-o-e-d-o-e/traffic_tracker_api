package net.joedoe.traffictracker.client;

import net.joedoe.traffictracker.service.DayService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Ignore("To avoid remote call to weather-api")
public class WindClientTest {
    @Mock
    private DayService service;
    private WindClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new WindClient(service);
    }

    @Test
    public void fetchWeather() {
        client.fetchWeather();
    }
}
