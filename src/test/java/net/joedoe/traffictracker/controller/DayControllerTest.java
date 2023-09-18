package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.exception.RestExceptionHandler;
import net.joedoe.traffictracker.hateoas.DayAssembler;
import net.joedoe.traffictracker.mapper.DayMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.DayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DayControllerTest {
    @InjectMocks
    DayController controller;
    @Mock
    private DayService service;
    @Mock
    DayAssembler assembler;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler()).build();
    }

    @Test
    public void getDayLatest() throws Exception {
        Day day = DaysInitTest.createDay(LocalDate.now().minusDays(1));
        DayDto dayDto = DayMapper.toDto(day, true, false);
        EntityModel<DayDto> resource = EntityModel.of(dayDto);

        when(service.getDayLatest()).thenReturn(dayDto);
        when(assembler.toModel(any())).thenReturn(resource);

        mockMvc.perform(get("/api/days/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(day.getTotal())))
                .andExpect(jsonPath("$.hours_flight", hasSize(24)))
                .andExpect(jsonPath("$.hours_wind", hasSize(24)));
    }

    @Test
    public void getDayByDate() throws Exception {
        Day day = DaysInitTest.createDay(LocalDate.now().minusDays(2));
        DayDto dayDto = DayMapper.toDto(day, true, false);
        EntityModel<DayDto> resource = EntityModel.of(dayDto);

        when(service.getDayByDate(date)).thenReturn(dayDto);
        when(assembler.toModel(any())).thenReturn(resource);

        mockMvc.perform(get("/api/days/" + DateTimeFormatter.ISO_DATE.format(date))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(day.getTotal())))
                .andExpect(jsonPath("$.hours_flight", hasSize(24)))
                .andExpect(jsonPath("$.hours_wind", hasSize(24)));
    }
}
