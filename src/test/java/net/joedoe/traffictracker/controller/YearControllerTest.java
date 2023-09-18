package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.exception.RestExceptionHandler;
import net.joedoe.traffictracker.hateoas.YearAssembler;
import net.joedoe.traffictracker.mapper.YearMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.YearService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class YearControllerTest {
    @InjectMocks
    private YearController controller;
    @Mock
    private YearService service;
    @Mock
    private YearAssembler assembler;
    private static MockMvc mockMvc;
    private final LocalDate date = LocalDate.now().withDayOfMonth(1).withMonth(1);
    private final List<Day> days = DaysInitTest.createDays(LocalDate.now().getDayOfYear() - 1);


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler()).build();
    }

    @Test
    public void getYearLatest() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        YearDto yearDto = YearMapper.toDto(date, days, true, false);
        EntityModel<YearDto> resource = EntityModel.of(yearDto);

        when(service.getYearLatest()).thenReturn(yearDto);
        when(assembler.toModel(any())).thenReturn(resource);

        mockMvc.perform(get("/api/years/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)));
    }

    @Test
    public void getYearByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        YearDto yearDto = YearMapper.toDto(date, days, true, true);
        EntityModel<YearDto> resource = EntityModel.of(yearDto);

        when(service.getYearByDate(date)).thenReturn(yearDto);
        when(assembler.toModel(any())).thenReturn(resource);

        mockMvc.perform(get("/api/years/" + LocalDate.now().getYear())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)));
    }
}