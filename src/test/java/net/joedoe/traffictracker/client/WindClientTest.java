package net.joedoe.traffictracker.client;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.WindDto;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

@Slf4j
@Disabled("To avoid remote call to weatherbit-api")
@SpringBootTest
public class WindClientTest {
    @Autowired
    private WindClient client;

    @Test
    public void fetchWeather() {
        WindDto windDto = client.fetchWind();
        log.info(windDto.toString());
    }
}
