package net.joedoe.traffictracker.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastHour;
import net.joedoe.traffictracker.model.ForecastScore;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class ForecastClient {
    private final ObjectMapper mapper = new ObjectMapper();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String windUrl;
    private final String baseUrl;
    private final OkHttpClient client = new OkHttpClient();

    public ForecastClient(@Value("${forecast.wind-url}") String windUrl,
                          @Value("${forecast.base-url}") String baseUrl) {
        this.windUrl = windUrl;
        this.baseUrl = baseUrl;
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<ForecastDay> fetchForecasts() {
        List<ForecastDay> res = new ArrayList<>();
        try (Response response = client.newCall(new Request.Builder().url(windUrl).build()).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            JsonNode ls = mapper.readTree(json).get("list");
            LocalDateTime dt = LocalDateTime.parse(ls.get(0).get("dt_txt").textValue(), formatter);
            ForecastDay day = new ForecastDay(dt.toLocalDate());
            for (JsonNode node : ls) {
                dt = LocalDateTime.parse(node.get("dt_txt").textValue(), formatter);
                int windDegree = node.get("wind").get("deg").asInt();
                if (dt.getHour() < 6) continue;
                if (dt.toLocalDate().isAfter(day.getDate())) {
                    res.add(day);
                    day = new ForecastDay(dt.toLocalDate());
                }
                day.getHours().add(new ForecastHour(dt.toLocalTime(), windDegree));
            }
            log.info("Forecast wind url: " + windUrl);
            log.info("Winds for forecasts fetched");
        } catch (IOException e) {
            log.info(e.getMessage());
            return null;
        }
        StringBuilder sb = new StringBuilder("{\"winds\":[");
        res.forEach(day -> day.getHours().forEach(hour -> sb.append(hour.getWindDegree()).append(",")));
        sb.deleteCharAt(sb.length() - 1).append("]}");
        RequestBody body = RequestBody.create(sb.toString(), MediaType.parse("text/plain"));
        try (Response response = client.newCall(new Request.Builder().url(baseUrl + "predict").method("POST", body).build()).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            JsonNode ls = mapper.readTree(json).get("predictions");
            int i = 0;
            for (ForecastDay day : res) {
                float sum = 0;
                for (ForecastHour hour : day.getHours()) {
                    float prob = (float) ls.get(i++).asDouble();
                    hour.setProbability(prob);
                    sum += prob;
                }
                day.setProbability((float) (Math.round(sum / day.getHours().size() * 100.0) / 100.0));
            }
            log.info("Forecasts url: " + baseUrl + "predict");
            log.info("Request body: " + sb);
            log.info("Forecasts fetched");
            res.forEach(f -> log.info(f.getDate() + ": " + f.getProbability()));
        } catch (IOException e) {
            log.info(e.getMessage());
            return null;
        }
        return res;
    }

    public ForecastScore fetchScores(List<Integer> flights, List<Integer> winds) {
        StringBuilder sb = new StringBuilder("{\"flights\":[");
        flights.forEach(f -> sb.append(f).append(","));
        sb.deleteCharAt(sb.length() - 1).append("],").append("\"winds\":[");
        winds.forEach(w -> sb.append(w).append(","));
        sb.deleteCharAt(sb.length() - 1).append("]}");
        RequestBody body = RequestBody.create(sb.toString(), MediaType.parse("text/plain"));
        try (Response response = client.newCall(new Request.Builder().url(baseUrl + "score").method("POST", body).build()).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            ForecastScore res = mapper.readValue(json, ForecastScore.class);
            log.info("Forecast scores url: " + baseUrl + "score");
            log.info("Request body: " + sb);
            log.info("Forecast scores fetched: " + res);
            return res;
        } catch (IOException e) {
            log.info(e.getMessage());
            return null;
        }
    }
}
