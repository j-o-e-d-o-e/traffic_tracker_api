package net.joedoe.traffictracker.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.WeatherClient.DataWrapper.WindData;
import net.joedoe.traffictracker.model.Wind;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.utils.PropertiesHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@PropertySource("classpath:locale.properties")
@Slf4j
@Component
public class WeatherClient {
    private final DayService service;
    private final ObjectMapper mapper = new ObjectMapper();
    private String url;

    public WeatherClient(DayService service) {
        this.service = service;
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            this.url = PropertiesHandler.getProperties("src/main/resources/weather.properties").getProperty("url.weather");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 30 6-23 * * *", zone = "${timezone}")
    public void fetchWeather() {
        try {
            Response response = new OkHttpClient().newCall(new Request.Builder().url(url).build()).execute();
            String json = Objects.requireNonNull(response.body()).string();
            WindData windData = mapper.readValue(json, DataWrapper.class).data[0];
            Wind wind = new Wind();
            wind.setDate(LocalDateTime.now());
            wind.setDeg(windData.wind_dir);
            wind.setSpeed(Math.round(windData.wind_spd * 3600 / 1000f * 100) / 100f);
            service.addWind(wind);
            log.info(wind.toString());
        } catch (IOException e) {
            e.printStackTrace();
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