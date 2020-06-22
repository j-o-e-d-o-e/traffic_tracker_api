package net.joedoe.traffictracker.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.repo.ForecastRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@PropertySource({"classpath:forecast.properties", "classpath:locale.properties"})
@Component
@Slf4j
public class ForecastClient {
    private OkHttpClient client;
    private ObjectMapper mapper = new ObjectMapper();
    private ForecastRepository repository;
    @Value("${forecastUrl}")
    private String url;

    public ForecastClient(ForecastRepository repository) {
        this.repository = repository;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Scheduled(cron = "0 0 0,6,9,12,15,18,21 * * *", zone = "${timezone}")
    public void predict() {
        try {
            long start = System.currentTimeMillis();
            Response response = client.newCall(new Request.Builder().url(url + "predict").build()).execute();
            long diff = (System.currentTimeMillis() - start) / 1000;
            log.info(diff + " secs passed to fetch predictions");

            String json = Objects.requireNonNull(response.body()).string();
            List<ForecastDay> days = mapper.readValue(json, new TypeReference<List<ForecastDay>>() {
            });
            repository.deleteAll();
            repository.saveAll(days);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
