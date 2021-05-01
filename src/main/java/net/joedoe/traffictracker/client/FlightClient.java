package net.joedoe.traffictracker.client;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.MapData;
import net.joedoe.traffictracker.model.MapData.CurrentFlight;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.util.PropertiesHandler;
import org.opensky.api.OpenSkyApi;
import org.opensky.api.OpenSkyApi.BoundingBox;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FlightClient {
    private final DayService service;
    private final OpenSkyApi api = new OpenSkyApi();
    private BoundingBox box;
    private int minHeading;
    private int maxHeading;
    private int minAltitude;
    private int maxAltitude;
    private int minSpeed;
    private int timeout;
    private float inLongitude;
    private float inLatitude;
    private Properties airlines;
    private List<CurrentFlight> currentFlights = new ArrayList<>();
    private boolean updated;

    public FlightClient(DayService service) {
        this.service = service;
        try {
            Properties prop = PropertiesHandler.getProperties("src/main/resources/flight.properties");
            float minLatitude = Float.parseFloat(prop.getProperty("min.latitude"));
            float minLongitude = Float.parseFloat(prop.getProperty("min.longitude"));
            float maxLatitude = Float.parseFloat(prop.getProperty("max.latitude"));
            float maxLongitude = Float.parseFloat(prop.getProperty("max.longitude"));
            this.box = new BoundingBox(minLatitude, maxLatitude, minLongitude, maxLongitude);
            this.inLatitude = Float.parseFloat(prop.getProperty("in.latitude"));
            this.inLongitude = Float.parseFloat(prop.getProperty("in.longitude"));
            this.minHeading = Integer.parseInt(prop.getProperty("min.heading"));
            this.maxHeading = Integer.parseInt(prop.getProperty("max.heading"));
            this.minAltitude = Integer.parseInt(prop.getProperty("min.geo.altitude"));
            this.maxAltitude = Integer.parseInt(prop.getProperty("max.geo.altitude"));
            this.minSpeed = Integer.parseInt(prop.getProperty("min.speed"));
            this.timeout = Integer.parseInt(prop.getProperty("timeout"));
            this.airlines = PropertiesHandler.getProperties("src/main/resources/airlinesList.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 20000)
    void fetchFlights() {
        currentFlights = currentFlights.stream().filter(this::checkTimeout)
                .peek(p -> p.setUpdated(false)).collect(Collectors.toList());
        log.info("Current data size: " + currentFlights.size());
        OpenSkyStates states = null;
        try {
            states = api.getStates(0, null, box);
        } catch (IOException e) {
            updated = false;
            log.info("Exception occurred: " + e.getMessage());
        }
        if (states == null) {
            updated = false;
            return;
        }
        updated = true;
        List<StateVector> vectors = (List<StateVector>) states.getStates();
        if (vectors == null || vectors.size() == 0) {
            log.info("No vectors found.");
            return;
        }
        log.info("Found vectors: " + vectors.size());
        for (StateVector vector : vectors) {
            if (!isValidVector(vector)) continue;
            String icao = vector.getIcao24();
            CurrentFlight currentFlight = currentFlights.stream()
                    .filter(p -> p.getIcao().equals(icao)).findFirst().orElse(null);
            if (currentFlight == null) {
                if (isValidNewFlight(vector)) {
                    Flight flight = new Flight();
                    flight.setIcao(icao);
                    flight.setDate(LocalDateTime.now());
                    flight.setAltitude(getAltitude(vector));
                    flight.setSpeed(getSpeed(vector));
                    String callsign = vector.getCallsign();
                    if (callsign != null) {
                        String airline = callsign.substring(0, 3);
                        flight.setAirline(airline);
                        flight.setAirlineName(airlines.getProperty(airline));
                    }
                    service.addFlight(flight);
                    currentFlight = new CurrentFlight(flight, vector.getLongitude(), vector.getLatitude());
                    this.currentFlights.add(currentFlight);
                    log.info("New flight: " + currentFlight.toString());
                }
            } else {
                currentFlight.setDate(LocalDateTime.now());
                currentFlight.setAltitude(getAltitude(vector));
                currentFlight.setSpeed(getSpeed(vector));
                Double longitude = vector.getLongitude();
                Double latitude = vector.getLatitude();
                if (longitude != null && latitude != null) {
                    currentFlight.setLongitude(longitude);
                    currentFlight.setLatitude(latitude);
                }
                currentFlight.setUpdated(true);
                log.info("Updated current flight: " + currentFlight.toString());
            }
        }
    }

    private boolean checkTimeout(CurrentFlight flight) {
        return flight.getDate().isAfter(LocalDateTime.now().minusMinutes(timeout));
    }

    private boolean isValidVector(StateVector vector) {
        try {
            Double heading = vector.getHeading();
            return heading > minHeading && heading < maxHeading;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean isValidNewFlight(StateVector vector) {
        try {
            int altitude = getAltitude(vector);
            return vector.getLongitude() > inLongitude && vector.getLatitude() > inLatitude &&
                    altitude > minAltitude && altitude < maxAltitude &&
                    getSpeed(vector) > minSpeed;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private int getAltitude(StateVector vector) {
        Double altitude = vector.getGeoAltitude() == null ? vector.getBaroAltitude() : vector.getGeoAltitude();
        if (altitude != null) return (int) Math.round(altitude);
        else return 0;
    }

    private int getSpeed(StateVector vector) {
        Double velocity = vector.getVelocity();
        if (velocity != null) return (int) Math.round(velocity * 60 * 60 / 1000);
        else return 0;
    }

    public MapData getCurrentFlights() {
        List<CurrentFlight> flights = currentFlights.stream()
                .filter(CurrentFlight::isUpdated).collect(Collectors.toList());
        return new MapData(flights, updated);
    }
}
