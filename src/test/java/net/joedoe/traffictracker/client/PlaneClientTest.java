package net.joedoe.traffictracker.client;

import net.joedoe.traffictracker.service.DayService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PlaneClientTest {
    @Mock
    private DayService service;
    private PlaneClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        client = new PlaneClient(service);
    }

    @Test
    public void fetchPlanes() {
        client.fetchPlanes();
    }
}