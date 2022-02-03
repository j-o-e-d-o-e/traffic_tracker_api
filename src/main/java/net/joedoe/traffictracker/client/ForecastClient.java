package net.joedoe.traffictracker.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastScore;
import net.joedoe.traffictracker.repo.ForecastRepository;
import net.joedoe.traffictracker.repo.ForecastScoreRepository;
import net.joedoe.traffictracker.util.PropertiesHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@PropertySource("classpath:locale.properties")
@Component
@Slf4j
public class ForecastClient {
    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ForecastRepository repository;
    private final ForecastScoreRepository scoreRepository;
    private String url;

    public ForecastClient(ForecastRepository repository, ForecastScoreRepository scoreRepository) {
        this.repository = repository;
        this.scoreRepository = scoreRepository;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            this.url = PropertiesHandler.getProperties("src/main/resources/forecast.properties").getProperty("url-forecast");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Scheduled(cron = "0 0 0,3,6,9,12,15,18,21 * * *", zone = "${timezone}")
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

//    @Scheduled(cron = "0 0 0 * * *", zone = "${timezone}")
    public void score() {
        try {
            long start = System.currentTimeMillis();
            Response response = client.newCall(new Request.Builder().url(url + "score").build()).execute();
            long diff = (System.currentTimeMillis() - start) / 1000;
            log.info(diff + " secs passed to fetch scores");

            String json = Objects.requireNonNull(response.body()).string();
            ForecastScore score = mapper.readValue(json, ScoreWrapper.class).score;
            scoreRepository.deleteAll();
            scoreRepository.save(score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Setter
    static class ScoreWrapper {
        ForecastScore score;
    }
}
