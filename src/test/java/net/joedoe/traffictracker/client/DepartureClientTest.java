package net.joedoe.traffictracker.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@Disabled("To avoid remote call to opensky-network-api")
@SpringBootTest
public class DepartureClientTest {
    @Autowired
    private DepartureClient client;

    @Test
    public void fetchDepartures() {
        List<DepartureClient.Departure> departures = client.fetchDepartures();
        for (DepartureClient.Departure depart : departures) log.info(depart.toString());
    }
}

