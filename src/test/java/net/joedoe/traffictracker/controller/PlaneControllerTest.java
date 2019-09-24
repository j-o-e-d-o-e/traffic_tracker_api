package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.PlanesInitTest;
import net.joedoe.traffictracker.exception.RestResponseEntityExceptionHandler;
import net.joedoe.traffictracker.mapper.PlaneMapper;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.PlaneService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlaneControllerTest {
    @Mock
    private PlaneService service;
    private MockMvc mockMvc;
    private List<Plane> planes = PlanesInitTest.createPlanes(LocalDate.now());

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PlaneController controller = new PlaneController(service, new PlaneMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
    }

    @Test
    public void getPlanesByDate() {
        Page<Plane> page = new PageImpl<>(planes, PageRequest.of(0, 20), 1);

        when(service.getPlanesByDate(any(LocalDate.class), any())).thenReturn(page);

//        String date = DateTimeFormatter.ISO_DATE.format(LocalDate.now());
//        mockMvc.perform(get("/planes/" + date)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
    }

    @Test
    public void getPlaneById() throws Exception {
        Plane plane = planes.get(0);

        when(service.getPlaneById(anyLong())).thenReturn(plane);

        mockMvc.perform(get("/planes/id/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.icao_24", equalTo(plane.getIcao())));
    }

    @Test
    public void getPlanesByIcao() {
        Page<Plane> page = new PageImpl<>(planes, PageRequest.of(0, 20), 1);

        when(service.getPlanesByIcao(anyString(), any())).thenReturn(page);
//
//        mockMvc.perform(get("/planes/icao24/" + plane.getIcao())
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].icao_24", equalTo(plane.getIcao())));
    }

    @Test
    public void getPlanesWithMaxAltitude() throws Exception {
        Plane plane = planes.get(2);

        when(service.getPlanesWithMaxAltitude()).thenReturn(Collections.singletonList(plane));

        mockMvc.perform(get("/planes/max/altitude")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].altitude", equalTo(plane.getAltitude())));
    }

    @Test
    public void getPlanesWithMaxSpeed() throws Exception {
        Plane plane = planes.get(2);

        when(service.getPlanesWithMaxSpeed()).thenReturn(Collections.singletonList(plane));

        mockMvc.perform(get("/planes/max/speed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].speed", equalTo(plane.getSpeed())));
    }

    @Test
    public void getPlanesWithMinAltitude() throws Exception {
        Plane plane = planes.get(1);

        when(service.getPlanesWithMinAltitude()).thenReturn(Collections.singletonList(plane));

        mockMvc.perform(get("/planes/min/altitude")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].altitude", equalTo(plane.getAltitude())));
    }

    @Test
    public void getPlanesWithMinSpeed() throws Exception {
        Plane plane = planes.get(0);

        when(service.getPlanesWithMinSpeed()).thenReturn(Collections.singletonList(plane));

        mockMvc.perform(get("/planes/min/speed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].speed", equalTo(plane.getSpeed())));
    }
}
