package net.joedoe.traffictracker.client;

import net.joedoe.traffictracker.service.DayService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Ignore("To avoid remote call to opensky-network")
public class FlightClientTest {
    @Mock
    private DayService service;
    private FlightClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        client = new FlightClient(service);
    }

    @Test
    public void fetchFlights() {
        client.fetchFlights();
    }
}