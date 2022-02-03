package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.exception.NotFoundExceptionHandler;
import net.joedoe.traffictracker.hateoas.YearAssembler;
import net.joedoe.traffictracker.mapper.YearMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.YearService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class YearControllerTest {
    @Mock
    private YearService service;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now().withDayOfMonth(1).withMonth(1);
    private final List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfYear() - 1);


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        YearController controller = new YearController(service, new YearAssembler());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new NotFoundExceptionHandler()).build();
    }

    @Test
    public void getCurrentYear() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getYear(date)).thenReturn(YearMapper.toDto(date, days, true, true));

        mockMvc.perform(get("/api/years/current")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)));
    }

    @Test
    public void getYearByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();

        when(service.getYear(date)).thenReturn(YearMapper.toDto(date, days, true, true));

        mockMvc.perform(get("/api/years/" + LocalDate.now().getYear())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)));
    }
}