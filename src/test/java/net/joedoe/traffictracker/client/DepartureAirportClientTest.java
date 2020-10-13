package net.joedoe.traffictracker.client;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.PlanesInitH2;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.PlaneService;
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
public class DepartureAirportClientTest {
    @Mock
    private PlaneService service;
    @Mock
    private DayService dayService;
    private DepartureAirportClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        client = new DepartureAirportClient(service, dayService);
    }

    @Ignore
    @Test
    public void fetchDepartures() {
        // given
        LocalDate date = LocalDate.now();
        List<Plane> planes = Objects.requireNonNull(PlanesInitH2.getPlanes(date.minusDays(1))).stream().
                peek(p -> p.setDepartureAirport(null)).collect(Collectors.toList());
        Plane plane = planes.get(0);
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        long end = Timestamp.valueOf(dateTime).getTime() / 1000;
        // when
        List<DepartureAirportClient.PlaneDepart> planesDepart = client.fetchDepartures(end);
        when(service.getPlanesListByDate(any())).thenReturn(planes);
        assertNull(plane.getDepartureAirport());
        client.updatePlanes(planesDepart, date);
        // then
        float planesWithDepartInfoRatio = planes.stream().filter(p -> p.getDepartureAirport() != null).count() / (float) planes.size();
        log.info(String.valueOf(planesWithDepartInfoRatio));
        assertTrue(planesWithDepartInfoRatio > 0.6); // e.g. 2020-09-07: 0.7553192
        assertNotNull(plane.getDepartureAirport());
    }

//    @Ignore
    @Test
    public void fetchDeparturesDate() {
        // given
        LocalDate date = LocalDate.of(2020, 10, 8); // for 2020-10-07
        List<Plane> planes = Objects.requireNonNull(PlanesInitH2.getPlanes(date.minusDays(1))).stream().
                peek(p -> p.setDepartureAirport(null)).collect(Collectors.toList());
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        long end = Timestamp.valueOf(dateTime).getTime() / 1000;
        // when
        List<DepartureAirportClient.PlaneDepart> planesDepart = client.fetchDepartures(end);
        when(service.getPlanesListByDate(any())).thenReturn(planes);
        client.updatePlanes(planesDepart, date);
        // then
        float planesWithDepartInfoRatio = planes.stream().filter(p -> p.getDepartureAirport() != null).count() / (float) planes.size();
        assertTrue(planesWithDepartInfoRatio > 0.6);
        log.info("Planes with DepartInfo-Ratio: " + planesWithDepartInfoRatio);
    }
}
