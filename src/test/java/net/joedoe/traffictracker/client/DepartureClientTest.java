package net.joedoe.traffictracker.client;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.DepartureClient.Departure;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.FlightService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Ignore("To avoid remote call to opensky-network")
public class DepartureClientTest {
    @Mock
    private FlightService service;
    @Mock
    private DayService dayService;
    private DepartureClient client;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new DepartureClient(service, dayService);
    }

    @Test
    public void fetchDepartures() {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT);
        long end = Timestamp.valueOf(dateTime).getTime() / 1000;
        List<Departure> departures = client.fetchDepartures(end);
        for (Departure depart : departures) {
            log.info(depart.toString());
        }
    }
}
