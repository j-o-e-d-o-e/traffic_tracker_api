package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.exception.RestResponseEntityExceptionHandler;
import net.joedoe.traffictracker.mapper.YearMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.DayService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlaneYearControllerTest {
    @Mock
    private DayService service;
    private MockMvc mockMvc;
    private LocalDate date = LocalDate.now().withDayOfMonth(1).withMonth(1);
    private List<Day> days = DaysInitTest.createDays(date);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PlaneYearController controller = new PlaneYearController(service, new YearMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
    }

    @Test
    public void getCurrentYear() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        TemporalField weeksOfYear = WeekFields.of(LocaleContextHolder.getLocale()).weekOfWeekBasedYear();
        int weeks = (int) date.withDayOfMonth(1).withMonth(6).range(weeksOfYear).getMaximum();

        when(service.getYear(date)).thenReturn(days);

        mockMvc.perform(get("/planes/year")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.weeks", hasSize(weeks)));
    }

    @Test
    public void getYearByDate() throws Exception {
        int total = days.stream().mapToInt(Day::getTotal).sum();
        TemporalField weeksOfYear = WeekFields.of(LocaleContextHolder.getLocale()).weekOfWeekBasedYear();
        int weeks = (int) date.withDayOfMonth(1).withMonth(6).range(weeksOfYear).getMaximum();

        when(service.getYear(date)).thenReturn(days);

        String dateFormat = DateTimeFormatter.ISO_DATE.format(LocalDate.now());
        mockMvc.perform(get("/planes/year/" + dateFormat)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", equalTo(total)))
                .andExpect(jsonPath("$.weeks", hasSize(weeks)));
    }
}