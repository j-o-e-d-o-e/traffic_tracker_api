package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.exception.NotFoundExceptionHandler;
import net.joedoe.traffictracker.hateoas.WeekAssembler;
import net.joedoe.traffictracker.mapper.WeekMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.WeekService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WeekControllerTest {
    @Mock
    private WeekService service;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now().with(DayOfWeek.MONDAY);
    private final List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfWeek().getValue() - 1);

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        WeekController controller = new WeekController(service, new WeekAssembler());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new NotFoundExceptionHandler()).build();
    }

    @Test
    public void getCurrentWeek() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getWeek(date)).thenReturn(WeekMapper.toDto(date, days));

        mockMvc.perform(get("/api/weeks/current")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.weekdays", hasSize(7)));
    }

    @Test
    public void getWeekByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getWeek(date)).thenReturn(WeekMapper.toDto(date, days));

        String dateFormat = DateTimeFormatter.ISO_DATE.format(date);
        mockMvc.perform(get("/api/weeks/" + dateFormat)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.weekdays", hasSize(7)));
    }
}
