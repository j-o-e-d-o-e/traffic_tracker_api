package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.exception.NotFoundExceptionHandler;
import net.joedoe.traffictracker.hateoas.DayAssembler;
import net.joedoe.traffictracker.mapper.DayMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DeviceRepository;
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
    @Mock
    private DeviceRepository userRepository;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        DayController controller = new DayController(service, new DayAssembler(), userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new NotFoundExceptionHandler()).build();
    }

    @Test
    public void getCurrentDay() throws Exception {
        Day day = DaysInitTest.createDay(LocalDate.now());

        when(service.getDayByDate(date)).thenReturn(DayMapper.toDto(day, true, false));
        mockMvc.perform(get("/api/days/current")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(day.getTotal())))
                .andExpect(jsonPath("$.hours_wind", hasSize(LocalDateTime.now().getHour() + 1)));
    }

    @Test
    public void getDayByDate() throws Exception {
        Day day = DaysInitTest.createDay(LocalDate.now().minusDays(1));

        when(service.getDayByDate(date)).thenReturn(DayMapper.toDto(day, true, true));

        String dateFormat = DateTimeFormatter.ISO_DATE.format(date);
        mockMvc.perform(get("/api/days/" + dateFormat)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(day.getTotal())))
                .andExpect(jsonPath("$.hours_flight", hasSize(24)))
                .andExpect(jsonPath("$.hours_wind", hasSize(24)));
    }
}
