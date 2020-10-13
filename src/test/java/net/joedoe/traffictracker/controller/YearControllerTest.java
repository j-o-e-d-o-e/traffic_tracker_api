package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.exception.RestResponseEntityExceptionHandler;
import net.joedoe.traffictracker.mapper.YearMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.DayService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class YearControllerTest {
    @Mock
    private DayService service;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now().withDayOfMonth(1).withMonth(1);
    private final List<Day> days = DaysInit.createDays(LocalDate.now().getDayOfYear() - 1);


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        YearController controller = new YearController(service, new YearMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
    }

    @Test
    public void getCurrentYear() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getYear(date)).thenReturn(days);

        mockMvc.perform(get("/planes/year")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)));
    }

    @Test
    public void getYearByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getYear(date)).thenReturn(days);

        mockMvc.perform(get("/planes/year/" + LocalDate.now().getYear())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)));
    }
}