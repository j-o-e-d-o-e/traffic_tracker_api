package net.joedoe.traffictracker.client;

import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.PlaneService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Ignore("To avoid remote call to opensky-network")
public class FlightClientTest {
    @Mock
    private DayService dayService;
    private FlightClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new FlightClient(dayService);
    }

    @Test
    public void fetchFlights() {
        client.fetchFlights();
    }
}