package net.joedoe.traffictracker.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.PlaneService;
import net.joedoe.traffictracker.utils.PropertiesHandler;
import okhttp3.*;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.*;
import java.util.*;

@PropertySource("classpath:locale.properties")
@Slf4j
@Component
public class DepartureAirportClient {
    private final PlaneService service;
    private final DayService dayService;
    public String url;
    public String arrivalAirport;
    public String timezone;
    private Properties departureAirports;
    private final OkHttpClient client;
    private int fails = 0;

    public DepartureAirportClient(PlaneService service, DayService dayService) {
        this.service = service;
        this.dayService = dayService;
        this.client = new OkHttpClient();
        this.client.retryOnConnectionFailure();
        try {
            Properties prop = PropertiesHandler.getProperties("src/main/resources/departureAirport.properties");
            this.url = prop.getProperty("url");
            this.arrivalAirport = prop.getProperty("airport");
            this.timezone = prop.getProperty("timezone");
            this.departureAirports = PropertiesHandler.getProperties("src/main/resources/departureAirportsList.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 45 5 * * *", zone = "${timezone}")
    public void getDepartures() {
        fails = 0;
        long now = (System.currentTimeMillis() / 1000) + (2 * 60 * 60); // + offset for timezone
        long end = now - (now % (24 * 60 * 60)); // for midnight
        List<PlaneDepart> planes = fetchDepartures(end);
        if (planes == null)
            return;
        LocalDate date = LocalDate.now().minusDays(1);
        updatePlanes(planes, date);
        dayService.setDepartures(date);
    }

    public List<PlaneDepart> fetchDepartures(long end) {
        try {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
            urlBuilder.addQueryParameter("airport", arrivalAirport);
            long begin = end - (24 * 60 * 60); // one day before
            urlBuilder.addQueryParameter("begin", String.valueOf(begin));
            urlBuilder.addQueryParameter("end", String.valueOf(end));
            String url = urlBuilder.build().toString();
            log.info("URL: " + url);
            Request request = new Request.Builder().url(url).build();

            Response response = client.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(Objects.requireNonNull(
                    response.body()).string(), new TypeReference<List<PlaneDepart>>() {
            });
        } catch (IOException e) {
            log.info(e.getMessage());
            fails++;
            if (fails < 3) {
                fetchDepartures(end);
            }
        }
        return null;
    }

    public void updatePlanes(List<PlaneDepart> planesDepart, LocalDate date) {
        log.info("Total fetched from api: " + planesDepart.size());
        List<Plane> planes = this.service.getPlanesListByDate(date);
        log.info("Total fetched from db: " + planes.size());
        for (PlaneDepart planeDepart : planesDepart) {
            if (planeDepart.estDepartureAirport == null)
                continue;
            LocalDateTime lastSeen = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(planeDepart.lastSeen * 1000),
                    TimeZone.getTimeZone(timezone).toZoneId());
            Optional<Plane> plane = planes.stream().filter(p -> p.getDepartureAirport() == null &&
                    planeDepart.icao24.equals(p.getIcao()) &&
                    lastSeen.isBefore(p.getDate().plusMinutes(30)) &&
                    lastSeen.isAfter(p.getDate().minusMinutes(30))
            ).findFirst();
            plane.ifPresent(p -> {
                p.setDepartureAirport(planeDepart.estDepartureAirport);
                p.setDepartureAirportName(departureAirports.getProperty(planeDepart.estDepartureAirport));
                service.save(p);
                log.info(p.toString());
            });
        }
    }

    @Setter
    public static class PlaneDepart {
        String icao24; // "3c0c9d"
        long firstSeen; // 1599410857
        String estDepartureAirport; // "LCLK"
        long lastSeen; // 1599424531
        String estArrivalAirport; // "EDDL"

        @Override
        public String toString() {
            return "Plane{" +
                    "icao24='" + icao24 + '\'' +
                    ", lastSeen=" + lastSeen + ", " + new Date(lastSeen * 1000) +
                    ", estDepartureAirport='" + estDepartureAirport + '\'' +
                    ", estArrivalAirport='" + estArrivalAirport + '\'' +
                    ", firstSeen=" + firstSeen + ", " + new Date(firstSeen * 1000) +
                    '}';
        }
    }
}
