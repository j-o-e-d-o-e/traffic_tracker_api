package net.joedoe.traffictracker.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.FlightService;
import net.joedoe.traffictracker.util.PropertiesHandler;
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
public class DepartureClient {
    private final FlightService service;
    private final DayService dayService;
    public String url;
    public String arrivalAirport;
    public String timezone;
    private Properties departureAirports;
    private final OkHttpClient client;
    private int fails = 0;

    public DepartureClient(FlightService service, DayService dayService) {
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
        List<Departure> departures = fetchDepartures(end);
        if (departures == null)
            return;
        LocalDate date = LocalDate.now().minusDays(1);
        updateFlights(departures, date);
        dayService.setDepartures(date);
    }

    public List<Departure> fetchDepartures(long end) {
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
                    response.body()).string(), new TypeReference<List<Departure>>() {
            });
        } catch (IOException e) {
            log.info(e.getMessage());
            fails++;
            if (fails < 3) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                fetchDepartures(end);
            }
        }
        return null;
    }

    public void updateFlights(List<Departure> departures, LocalDate date) {
        log.info("Total fetched from api: " + departures.size());
        List<Flight> flights = this.service.getFlightsListByDate(date);
        log.info("Total fetched from db: " + flights.size());
        for (Departure departure : departures) {
            if (departure.estDepartureAirport == null)
                continue;
            LocalDateTime lastSeen = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(departure.lastSeen * 1000),
                    TimeZone.getTimeZone(timezone).toZoneId());
            Optional<Flight> flight = flights.stream().filter(f -> f.getDepartureAirport() == null &&
                    departure.icao24.equals(f.getIcao()) &&
                    lastSeen.isBefore(f.getDate().plusMinutes(30)) &&
                    lastSeen.isAfter(f.getDate().minusMinutes(30))
            ).findFirst();
            flight.ifPresent(p -> {
                p.setDepartureAirport(departure.estDepartureAirport);
                p.setDepartureAirportName(departureAirports.getProperty(departure.estDepartureAirport));
                service.save(p);
                log.info(p.toString());
            });
        }
    }

    @Setter
    public static class Departure {
        String icao24; // "3c0c9d"
        long firstSeen; // 1599410857
        String estDepartureAirport; // "LCLK"
        long lastSeen; // 1599424531
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
