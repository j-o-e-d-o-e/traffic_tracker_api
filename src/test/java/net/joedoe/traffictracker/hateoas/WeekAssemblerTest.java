package net.joedoe.traffictracker.hateoas;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.mapper.WeekMapper;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class WeekAssemblerTest {
    private final WeekAssembler assembler = new WeekAssembler();
    private final LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);

    @Test
    public void toModel(){
        WeekDto week = WeekMapper.toDto(startDate, DaysInitTest.createDays(LocalDate.now().getDayOfWeek().getValue() - 1), true, false);

        EntityModel<WeekDto> weekDto = assembler.toModel(week);

        assertEquals("/api/weeks/" + startDate.minusWeeks(1), weekDto.getRequiredLink("prev_week").getHref());
        assertFalse(weekDto.hasLink("next_week"));
        assertEquals("/api/days/" + startDate, weekDto.getRequiredLink("days").getHref());
        assertEquals("/api/months/" + startDate.getYear() +"/" + startDate.getMonthValue(), weekDto.getRequiredLink("month").getHref());
    }
}
