package net.joedoe.traffictracker.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.WindClient.DataWrapper.WindData;
import net.joedoe.traffictracker.dto.WindDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
public class WindClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String url;

    public WindClient(@Value("${wind-url}")String url) {
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.url = url;
    }

    public WindDto fetchWind() {
        try (Response response = new OkHttpClient().newCall(new Request.Builder().url(url).build()).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            WindData windData = mapper.readValue(json, DataWrapper.class).data[0];
            WindDto windDto = new WindDto();
            windDto.setDateTime(LocalDateTime.now());
            windDto.setDeg(windData.wind_dir);
            windDto.setSpeed(Math.round(windData.wind_spd * 3600 / 1000f * 100) / 100f);
            log.info("Wind url: " + url);
            log.info("Wind fetched: " + windDto);
            return windDto;
        } catch (IOException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Setter
    static class DataWrapper {
        WindData[] data;

        @Setter
        static class WindData {
            int wind_dir;
            float wind_spd;
        }
    }
}
