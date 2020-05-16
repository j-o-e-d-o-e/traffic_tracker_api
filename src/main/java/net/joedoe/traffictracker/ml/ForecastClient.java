package net.joedoe.traffictracker.ml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@PropertySource("classpath:ml.properties")
@Component
@Slf4j
public class ForecastClient {
    private ObjectMapper mapper = new ObjectMapper();
    @Value("${mlWeatherUrl}")
    private String url;

    public ForecastClient() {
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public DataWrapper fetch() {
        DataWrapper data = new DataWrapper();
        try {
            Response response = new OkHttpClient().newCall(new Request.Builder().url(url).build()).execute();
            String json = Objects.requireNonNull(response.body()).string();
            data = mapper.readValue(json, DataWrapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Data
    static class DataWrapper {
        Weather[] list;

        @Data
        static class Weather {
            String dt_txt;
            Wind wind;

            @Data
            static class Wind {
                int deg;
            }
        }
    }
}