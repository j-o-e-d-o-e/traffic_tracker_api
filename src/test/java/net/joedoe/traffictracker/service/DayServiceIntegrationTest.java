package net.joedoe.traffictracker.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@Disabled("To avoid remote call to opensky-network-api")
@SpringBootTest
public class DayServiceIntegrationTest {
    @Autowired
    private DayService service;

    @Test
    public void fetchDepartures() {
        service.fetchDepartures();
    }
}
