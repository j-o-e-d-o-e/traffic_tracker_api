package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.ForecastsInit;
import net.joedoe.traffictracker.exception.RestResponseEntityExceptionHandler;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastScore;
import net.joedoe.traffictracker.service.ForecastScoreService;
import net.joedoe.traffictracker.service.ForecastService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ForecastControllerTest {
    @Mock
    private ForecastService service;
    @Mock
    private ForecastScoreService scoreService;
    private MockMvc mockMvc;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ForecastController controller = new ForecastController(service, scoreService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
    }

    @Test
    public void getAll() throws Exception {
        List<ForecastDay> days = ForecastsInit.createDays();

        when(service.findAll()).thenReturn(days);
        mockMvc.perform(get("/api/forecasts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].probability", closeTo(days.get(0).getProbability(), 0.1f)))
                .andExpect(jsonPath("$[0].hours", hasSize(6)));
    }

    @Test
    public void getScore() throws Exception {
        ForecastScore score = ForecastsInit.createScore();

        when(scoreService.find()).thenReturn(score);
        mockMvc.perform(get("/api/forecasts/score")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precision", closeTo(score.getPrecision(), 0.1f)))
                .andExpect(jsonPath("$.mean_abs_error", closeTo(score.getMeanAbsoluteError(), 0.1f)));
    }

}