package net.joedoe.traffictracker.hateoas;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.mapper.DayMapper;
import org.junit.Test;
import org.springframework.hateoas.EntityModel;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Slf4j
public class DayAssemblerTest {
    private final DayAssembler assembler = new DayAssembler();


    @Test
    public void toModelToday(){
        LocalDate date = LocalDate.now();
        DayDto dayDto = DayMapper.toDto(DaysInitTest.createDay(date), true, false);
        EntityModel<DayDto> model = assembler.toModel(dayDto);

        assertEquals("/api/days/" + date.minusDays(1), model.getRequiredLink("prev_day").getHref());
        assertFalse(model.hasLink("next_day"));
        assertEquals(dayDto.isFlights(), model.hasLink("flights"));
        assertEquals("/api/flights/" + date, model.getRequiredLink("flights").getHref());
        assertEquals("/api/weeks/" + date.with(DayOfWeek.MONDAY), model.getRequiredLink("week").getHref());
    }

    @Test
    public void toModelYesterday(){
        LocalDate date = LocalDate.now().minusDays(1);
        DayDto dayDto = DayMapper.toDto(DaysInitTest.createDay(date), true, true);

        EntityModel<DayDto> model = assembler.toModel(dayDto);

        assertEquals("/api/days/" + date.minusDays(1), model.getRequiredLink("prev_day").getHref());
        assertEquals("/api/days/" + date.plusDays(1), model.getRequiredLink("next_day").getHref());
        assertEquals("/api/flights/" + date, model.getRequiredLink("flights").getHref());
        assertEquals("/api/weeks/" + date.with(DayOfWeek.MONDAY), model.getRequiredLink("week").getHref());
    }

}