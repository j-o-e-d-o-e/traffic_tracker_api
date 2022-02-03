package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.FlightsInitTest;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.exception.NotFoundExceptionHandler;
import net.joedoe.traffictracker.hateoas.FlightAssembler;
import net.joedoe.traffictracker.mapper.FlightMapper;
import net.joedoe.traffictracker.repo.DeviceRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class FlightControllerTest {
    @Mock
    private FlightService service;
    @Mock
    private DeviceRepository deviceRepository;
    private MockMvc mockMvc;
    private List<FlightDto> flights;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        FlightController controller = new FlightController(service, new FlightAssembler(), deviceRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .setControllerAdvice(new NotFoundExceptionHandler())
                .build();
        flights = FlightsInitTest.createFlights().stream().map(FlightMapper::toDto).collect(Collectors.toList());
    }

    @Test
    public void getFlightsByDate() throws Exception {
        Page<FlightDto> page = new PageImpl<>(flights, PageRequest.of(0, 20), 1);

        when(service.getByDate(any(LocalDate.class), any())).thenReturn(page);

        String date = DateTimeFormatter.ISO_DATE.format(LocalDate.now().minusDays(1));
        log.info(date);

        mockMvc.perform(get("/api/flights/" + date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].icao_24", equalTo(flights.get(0).getIcao_24())));
    }
}
