package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.exception.RestResponseEntityExceptionHandler;
import net.joedoe.traffictracker.mapper.MonthMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlaneMonthControllerTest {
    @Mock
    private DayService service;
    private MockMvc mockMvc;
    private LocalDate date = LocalDate.now().withDayOfMonth(1);
    private List<Day> days = DaysInitTest.createDays(date);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PlaneMonthController controller = new PlaneMonthController(service, new MonthMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
    }

    @Test
    public void getCurrentMonth() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getMonth(date)).thenReturn(days);

        mockMvc.perform(get("/planes/month")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.days", hasSize(date.getMonth().length(date.isLeapYear()))));
    }

    @Test
    public void getMonthByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getMonth(date)).thenReturn(days);

        String dateFormat = DateTimeFormatter.ISO_DATE.format(date);
        mockMvc.perform(get("/planes/month/" + dateFormat)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.days", hasSize(date.getMonth().length(date.isLeapYear()))));
    }
}
