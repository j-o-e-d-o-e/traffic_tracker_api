package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.exception.RestResponseEntityExceptionHandler;
import net.joedoe.traffictracker.mapper.DayMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.DayService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class DayControllerTest {
    @Mock
    private DayService service;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DayController controller = new DayController(service, new DayMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
    }

    @Test
    public void getCurrentDay() throws Exception {
        Day day = DaysInit.createDay(LocalDate.now());

        when(service.getDay(date)).thenReturn(day);
        mockMvc.perform(get("/planes/day")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(day.getTotal())))
                .andExpect(jsonPath("$.hours_wind", hasSize(LocalDateTime.now().getHour() + 1)));
    }

    @Test
    public void getDayByDate() throws Exception {
        Day day = DaysInit.createDay(LocalDate.now().minusDays(1));

        when(service.getDay(date)).thenReturn(day);

        String dateFormat = DateTimeFormatter.ISO_DATE.format(date);
        mockMvc.perform(get("/planes/day/" + dateFormat)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(day.getTotal())))
                .andExpect(jsonPath("$.hours_plane", hasSize(24)))
                .andExpect(jsonPath("$.hours_wind", hasSize(24)));
    }

    @Test
    public void getDayById() throws Exception {
        Day day = DaysInit.createDay(LocalDate.now().minusDays(1));

        when(service.getDayById(day.getId())).thenReturn(day);

        mockMvc.perform(get("/planes/day/id/" + day.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(day.getTotal())))
                .andExpect(jsonPath("$.hours_plane", hasSize(24)))
                .andExpect(jsonPath("$.hours_wind", hasSize(24)));
    }
}