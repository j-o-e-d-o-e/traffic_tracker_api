package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.FlightsInit;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.exception.RestResponseEntityExceptionHandler;
import net.joedoe.traffictracker.hateoas.FlightAssembler;
import net.joedoe.traffictracker.mapper.FlightMapper;
import net.joedoe.traffictracker.service.FlightService;
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
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class FlightControllerTest {
    @Mock
    private FlightService service;
    private MockMvc mockMvc;
    private List<FlightDto> flights;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FlightController controller = new FlightController(service, new FlightAssembler());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
        flights = FlightsInit.createFlights().stream().map(FlightMapper::toDto).collect(Collectors.toList());
    }

    @Test
    public void getFlightsByDate() throws Exception {
        Page<FlightDto> page = new PageImpl<>(flights, PageRequest.of(0, 20), 1);

        when(service.getFlightsByDate(any(LocalDate.class), any())).thenReturn(page);

        String date = DateTimeFormatter.ISO_DATE.format(LocalDate.now().minusDays(1));
        log.info(date);

        mockMvc.perform(get("/api/flights/" + date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].icao_24", equalTo(flights.get(0).getIcao_24())));
    }

    @Test
    public void getFlightById() throws Exception {
        FlightDto flight = flights.get(0);

        when(service.getFlightById(anyLong())).thenReturn(flight);

        mockMvc.perform(get("/api/flights/id/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.icao_24", equalTo(flight.getIcao_24())));
    }

    @Test
    public void getFlightsByIcao() throws Exception {
        Page<FlightDto> page = new PageImpl<>(flights, PageRequest.of(0, 20), 1);
        String icao = flights.get(0).getIcao_24();

        when(service.getFlightsByIcao(anyString(), any())).thenReturn(page);

        mockMvc.perform(get("/api/flights/icao24/" + icao)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].icao_24", equalTo(icao)));
    }
}
