package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.exception.RestResponseEntityExceptionHandler;
import net.joedoe.traffictracker.hateoas.MonthAssembler;
import net.joedoe.traffictracker.mapper.MonthMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.DayService;
import net.joedoe.traffictracker.service.MonthService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MonthControllerTest {
    @Mock
    private MonthService service;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now().withDayOfMonth(1);
    private final List<Day> days = DaysInit.createDays(LocalDate.now().getDayOfMonth() - 1);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        MonthController controller = new MonthController(service, new MonthAssembler());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
    }

    @Test
    public void getCurrentMonth() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getMonth(date)).thenReturn(MonthMapper.toDto(date, days));

        mockMvc.perform(get("/api/months/current")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.days", hasSize(date.getMonth().length(date.isLeapYear()))));
    }

    @Test
    public void getMonthByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getMonth(date)).thenReturn(MonthMapper.toDto(date, days));

        System.out.println("/api/months/" + date.getYear() + "/" + date.getMonthValue());
        mockMvc.perform(get("/api/months/" + date.getYear() + "/" + date.getMonthValue())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.days", hasSize(date.getMonth().length(date.isLeapYear()))));
    }
}
