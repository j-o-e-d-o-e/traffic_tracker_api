package net.joedoe.traffictracker.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class DepartureClient {
    private final String url;
    private final String auth;
    private final String arrivalAirport;
    private final OkHttpClient client;

    public DepartureClient(@Value("${departure.url}") String url,
                           @Value("${departure.auth}") String auth,
                           @Value("${departure.arrival-airport}") String arrivalAirport) {
        this.client = new OkHttpClient();
        //noinspection ResultOfMethodCallIgnored
        this.client.retryOnConnectionFailure();
        this.url = url;
        this.auth = auth;
        this.arrivalAirport = arrivalAirport;
    }

    public List<Departure> fetchDepartures() {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        urlBuilder.addQueryParameter("airport", arrivalAirport);
        long now = (System.currentTimeMillis() / 1000);
        long end = now - (now % (24 * 60 * 60)); // for midnight
        long begin = end - (24 * 60 * 60); // one day before
        urlBuilder.addQueryParameter("begin", String.valueOf(begin));
        urlBuilder.addQueryParameter("end", String.valueOf(end));
        String url = urlBuilder.build().toString();
        log.info("Departure url: " + url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<>() {
            });
        } catch (IOException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Setter
    public static class Departure {
        public String icao24; // "3c0c9d" -> "3C0C9D"
        long firstSeen; // 1599410857
        public String estDepartureAirport; // "LCLK"
        public long lastSeen; // 1599424531
        String estArrivalAirport; // "EDDL"

        @Override
        public String toString() {
            return "Departure{" +
                    "icao24='" + icao24 + '\'' +
                    ", lastSeen=" + lastSeen + ", " + new Date(lastSeen * 1000) +
                    ", estDepartureAirport='" + estDepartureAirport + '\'' +
                    ", estArrivalAirport='" + estArrivalAirport + '\'' +
                    ", firstSeen=" + firstSeen + ", " + new Date(firstSeen * 1000) +
                    '}';
        }
    }
}
