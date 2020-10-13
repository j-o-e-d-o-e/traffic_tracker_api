package net.joedoe.traffictracker.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PlaneDto;
import net.joedoe.traffictracker.model.Plane;
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
public class PlanesInitH2 implements CommandLineRunner {
    private final DayService service;

    public PlanesInitH2(DayService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        List<Plane> planes = getPlanes(LocalDate.now().minusDays(1));
        if (planes != null)
            for (Plane plane : planes)
                service.addPlane(plane);
    }

    public static List<Plane> getPlanes(LocalDate date) {
        String URL = "https://traffic-tracker.herokuapp.com/planes/one/day/" + date;
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
        try {
            Response response = client.newCall(new Request.Builder().url(URL).build()).execute();
            List<PlaneDto> planeDtos = mapper.readValue(Objects.requireNonNull(
                    response.body()).string(), new TypeReference<List<PlaneDto>>() {
            });
            return toPlanes(planeDtos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    private static List<Plane> toPlanes(List<PlaneDto> planeDtos) {
        List<Plane> planes = new ArrayList<>();
        for (PlaneDto p : planeDtos) {
            Plane plane = new Plane();
            plane.setIcao(p.getIcao_24());
            plane.setDate(p.getDate_time());
            plane.setAltitude(p.getAltitude());
            plane.setSpeed(p.getSpeed());
            plane.setDepartureAirport(p.getDeparture_airport());
            plane.setDepartureAirportName(p.getDeparture_airport_name());
            plane.setAirline(p.getAirline());
            plane.setAirlineName(p.getAirline_name());
            planes.add(plane);
        }
        return planes;
    }
}
