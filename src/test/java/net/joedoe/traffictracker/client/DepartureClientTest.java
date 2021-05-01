package net.joedoe.traffictracker.client;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.FlightsInitH2;
import net.joedoe.traffictracker.client.DepartureClient.Departure;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.FlightService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@Ignore("To avoid remote call to this heroku-spring-api and opensky-network")
public class DepartureClientTest {
    @Mock
    private FlightService service;
    @Mock
    private DayService dayService;
    private DepartureClient client;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        client = new DepartureClient(service, dayService);
    }

    @Test
    public void fetchDepartures() {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT);
        long end = Timestamp.valueOf(dateTime).getTime() / 1000;
        List<Departure> departures = client.fetchDepartures(end);
        for (Departure depart : departures) {
            log.info(depart.toString());
        }
    }

    @Test
    public void fetchDeparturesAndUpdateFlights() {
        // given
        LocalDate date = LocalDate.of(2021, 4, 29); // date with flights > 0 AND within last 30 days
        List<Flight> flights = Objects.requireNonNull(FlightsInitH2.getFlightsFromMyApi(date.minusDays(1))).stream().
                peek(f -> f.setDepartureAirport(null)).collect(Collectors.toList());
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        long end = Timestamp.valueOf(dateTime).getTime() / 1000;
        // when
        List<Departure> departures = client.fetchDepartures(end);
        when(service.getFlightsListByDate(any())).thenReturn(flights);
        client.updateFlights(departures, date);
        // then
        float flightsWithDeparture = flights.stream().filter(f -> f.getDepartureAirport() != null).count() / (float) flights.size();
        log.info(String.valueOf(flightsWithDeparture));
        assertTrue(flightsWithDeparture > 0.6); // e.g. 2020-09-07: 0.7553192
    }
}
