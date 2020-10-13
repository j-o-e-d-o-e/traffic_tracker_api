package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.PlanesInit;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class PlaneControllerTest {
    @Mock
    private PlaneService service;
    private MockMvc mockMvc;
    private final List<Plane> planes = PlanesInit.createPlanes();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PlaneController controller = new PlaneController(service, new PlaneMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
    }

    @Test
    public void getPlanesByDate() throws Exception {
        Page<Plane> page = new PageImpl<>(planes, PageRequest.of(0, 20), 1);

        when(service.getPlanesByDate(any(LocalDate.class), any())).thenReturn(page);

        String date = DateTimeFormatter.ISO_DATE.format(LocalDate.now().minusDays(1));
        log.info(date);

        mockMvc.perform(get("/planes/" + date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].icao_24", equalTo(planes.get(0).getIcao())));
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
    public void getPlanesByIcao() throws Exception {
        Page<Plane> page = new PageImpl<>(planes, PageRequest.of(0, 20), 1);
        String icao = planes.get(0).getIcao();

        when(service.getPlanesByIcao(anyString(), any())).thenReturn(page);

        mockMvc.perform(get("/planes/icao24/" + icao)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].icao_24", equalTo(icao)));
    }
}
