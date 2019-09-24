package net.joedoe.traffictracker.client;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.DayService;
import org.opensky.api.OpenSkyApi;
import org.opensky.api.OpenSkyApi.BoundingBox;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PropertySource("classpath:planes.properties")
@Slf4j
@Component
public class PlaneClient {
    private DayService service;
    private List<Plane> planes = new ArrayList<>();
    private OpenSkyApi api = new OpenSkyApi();
    private BoundingBox box;
    @Value("${minLatitude}")
    float minLatitude;
    @Value("${maxLatitude}")
    float maxLatitude;
    @Value("${minLongitude}")
    float minLongitude;
    @Value("${maxLongitude}")
    float maxLongitude;
    @Value("${minHeading}")
    private int minHeading;
    @Value("${maxHeading}")
    private int maxHeading;
    @Value("${minGeoAltitude}")
    private int minAltitude;
    @Value("${maxGeoAltitude}")
    private int maxAltitude;
    @Value("${timeout}")
    private int timeout;

    public PlaneClient(DayService service) {
        this.service = service;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        box = new BoundingBox(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    @Scheduled(fixedRate = 15000)
    void fetchPlanes() {
        planes = planes.stream().filter(this::checkTimeout).collect(Collectors.toList());
        log.info("Current data size: " + planes.size());
        OpenSkyStates states = null;
        try {
            states = api.getStates(0, null, box);
        } catch (Exception e) {
            log.info("No states found.");
        }
        if (states == null) return;
        List<StateVector> vectors = (List<StateVector>) states.getStates();
        if (vectors == null || vectors.size() == 0) {
            log.info("No planes found.");
            return;
        }
        log.info("Number of all planes: " + vectors.size());
        vectors = vectors.stream().filter(this::include).collect(Collectors.toList());
        if (vectors.size() == 0) {
            log.info("No planes met requirements.");
            return;
        }
        log.info("Number of valid planes: " + vectors.size());
        for (StateVector vector : vectors) {
            Plane plane = new Plane();
            plane.setIcao(vector.getIcao24());
            plane.setDate(LocalDateTime.now());
            plane.setAltitude((int) Math.round(vector.getGeoAltitude()));
            plane.setSpeed((int) Math.round(vector.getVelocity() * 60 * 60 / 1000));
            log.info(plane.toString());
            this.planes.add(plane);
            service.addPlane(plane);
        }
    }

    private boolean checkTimeout(Plane plane) {
        return plane.getDate().isAfter(LocalDateTime.now().minusMinutes(timeout));
    }

    private boolean include(StateVector vector) {
        if (planes.stream().anyMatch(p -> p.getIcao().equals(vector.getIcao24()))) {
            return false;
        }
        try {
            return vector.getHeading() > minHeading && vector.getHeading() < maxHeading &&
                    vector.getGeoAltitude() > minAltitude && vector.getGeoAltitude() < maxAltitude;
        } catch (Exception e) {
            return false;
        }
    }
}
