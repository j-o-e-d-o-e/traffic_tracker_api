package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.exception.RestExceptionHandler;
import net.joedoe.traffictracker.hateoas.MonthAssembler;
import net.joedoe.traffictracker.mapper.MonthMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.MonthService;
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
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MonthControllerTest {
    @InjectMocks
    MonthController controller;
    @Mock
    private MonthService service;
    @Mock
    private MonthAssembler assembler;
    private MockMvc mockMvc;
    private final LocalDate date = LocalDate.now().withDayOfMonth(1);
    private final List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfMonth() - 1);

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler()).build();
    }

    @Test
    public void getMonthLatest() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        MonthDto monthDto = MonthMapper.toDto(date, days, true, false);
        EntityModel<MonthDto> resource = EntityModel.of(monthDto);

        when(service.getMonthLatest()).thenReturn(monthDto);
        when(assembler.toModel(any())).thenReturn(resource);

        mockMvc.perform(get("/api/months/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.days", hasSize(date.getMonth().length(date.isLeapYear()))));
    }

    @Test
    public void getMonthByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        MonthDto monthDto = MonthMapper.toDto(date, days, true, true);
        EntityModel<MonthDto> resource = EntityModel.of(monthDto);

        when(service.getMonthByDate(date)).thenReturn(monthDto);
        when(assembler.toModel(any())).thenReturn(resource);

        System.out.println("/api/months/" + date.getYear() + "/" + date.getMonthValue());
        mockMvc.perform(get("/api/months/" + date.getYear() + "/" + date.getMonthValue())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.days", hasSize(date.getMonth().length(date.isLeapYear()))));
    }
}
