package net.joedoe.traffictracker.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.service.DayService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Profile("h2")
@Order(4)
@Slf4j
@Component
public class FlightsInitH2 implements CommandLineRunner {
    private final DayService service;

    public FlightsInitH2(DayService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        List<Flight> flights = getFlightsFromMyApi(LocalDate.now().minusDays(1));
        if (flights != null)
            for (Flight flight : flights)
                service.addFlight(flight);
    }

    public static List<Flight> getFlightsFromMyApi(LocalDate date) {
        String URL = "https://traffic-tracker.herokuapp.com/api/flights/one/day/" + date;
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
        try {
            Response response = client.newCall(new Request.Builder().url(URL).build()).execute();
            List<FlightDto> flightDtos = mapper.readValue(Objects.requireNonNull(
                    response.body()).string(), new TypeReference<List<FlightDto>>() {
            });
            return toFlights(flightDtos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    private static List<Flight> toFlights(List<FlightDto> flightDtos) {
        List<Flight> flights = new ArrayList<>();
        for (FlightDto f : flightDtos) {
            Flight flight = new Flight();
            flight.setIcao(f.getIcao_24());
            flight.setDate(f.getDate_time());
            flight.setAltitude(f.getAltitude());
            flight.setSpeed(f.getSpeed());
            flight.setDepartureAirport(f.getDeparture_airport());
            flight.setDepartureAirportName(f.getDeparture_airport_name());
            flight.setAirline(f.getAirline());
            flight.setAirlineName(f.getAirline_name());
            flights.add(flight);
        }
        return flights;
    }
}
