package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.ForecastsInitTest;
import net.joedoe.traffictracker.exception.RestExceptionHandler;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastScore;
import net.joedoe.traffictracker.service.ForecastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ForecastControllerTest {
    @InjectMocks
    private ForecastController controller;
    @Mock
    private ForecastService service;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler()).build();
    }

    @Test
    public void getAll() throws Exception {
        List<ForecastDay> days = ForecastsInitTest.createForecastDays();

        when(service.getForecasts()).thenReturn(days);

        mockMvc.perform(get("/api/forecasts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].probability", closeTo(days.get(0).getProbability(), 0.1f)))
                .andExpect(jsonPath("$[0].hours", hasSize(6)));
    }

    @Test
    public void getScore() throws Exception {
        ForecastScore score = ForecastsInitTest.createScore();

        when(service.getScore()).thenReturn(score);

        mockMvc.perform(get("/api/forecasts/score")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precision", closeTo(score.getPrecision(), 0.1f)))
                .andExpect(jsonPath("$.mean_abs_error", closeTo(score.getMeanAbsoluteError(), 0.1f)));
    }

}