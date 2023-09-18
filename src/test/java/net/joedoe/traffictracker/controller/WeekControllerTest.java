package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.exception.RestExceptionHandler;
import net.joedoe.traffictracker.hateoas.WeekAssembler;
import net.joedoe.traffictracker.mapper.WeekMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.WeekService;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WeekControllerTest {
    @InjectMocks
    WeekController controller;
    @Mock
    private WeekService service;
    @Mock
    private WeekAssembler assembler;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now().with(DayOfWeek.MONDAY);
    private final List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfWeek().getValue() - 1);

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler()).build();
    }

    @Test
    public void getWeekLatest() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        WeekDto weekDto = WeekMapper.toDto(date, days, true, false);
        EntityModel<WeekDto> resource = EntityModel.of(weekDto);

        when(service.getWeekLatest()).thenReturn(weekDto);
        when(assembler.toModel(any())).thenReturn(resource);

        mockMvc.perform(get("/api/weeks/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.weekdays", hasSize(7)));
    }

    @Test
    public void getWeekByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        WeekDto weekDto = WeekMapper.toDto(date, days, true, true);
        EntityModel<WeekDto> resource = EntityModel.of(weekDto);

        when(service.getWeekByDate(date)).thenReturn(weekDto);
        when(assembler.toModel(any())).thenReturn(resource);

        String dateFormat = DateTimeFormatter.ISO_DATE.format(date);
        mockMvc.perform(get("/api/weeks/" + dateFormat)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.weekdays", hasSize(7)));
    }
}
