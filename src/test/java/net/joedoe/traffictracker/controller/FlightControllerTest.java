package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.FlightsInitTest;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.exception.RestExceptionHandler;
import net.joedoe.traffictracker.hateoas.FlightAssembler;
import net.joedoe.traffictracker.mapper.FlightMapper;
import net.joedoe.traffictracker.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FlightControllerTest {
    @InjectMocks
    FlightController controller;
    @Mock
    private FlightService service;
    @Mock
    private FlightAssembler assembler;
    private MockMvc mockMvc;
    private final List<FlightDto> flights = FlightsInitTest.createFlights().stream().map(FlightMapper::toDto).collect(Collectors.toList());

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void getFlightsLatest() throws Exception {
        Page<FlightDto> page = new PageImpl<>(flights, PageRequest.of(0, 20), 1);

        when(service.getFlightsForLatestDay(any())).thenReturn(page);
        for (FlightDto flight : flights) when(assembler.toModel(flight)).thenReturn(EntityModel.of(flight));


        mockMvc.perform(get("/api/flights/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].icao_24", equalTo(flights.get(0).getIcao_24())));
    }

    @Test
    public void getFlightsByDate() throws Exception {
        Page<FlightDto> page = new PageImpl<>(flights, PageRequest.of(0, 20), 1);

        when(service.getFlightsByDate(any(LocalDate.class), any())).thenReturn(page);
        for (FlightDto flight : flights) when(assembler.toModel(flight)).thenReturn(EntityModel.of(flight));

        mockMvc.perform(get("/api/flights/" + DateTimeFormatter.ISO_DATE.format(LocalDate.now().minusDays(1)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].icao_24", equalTo(flights.get(0).getIcao_24())));
    }
}
