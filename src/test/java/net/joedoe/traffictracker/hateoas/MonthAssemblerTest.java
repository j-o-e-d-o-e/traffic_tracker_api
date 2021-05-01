package net.joedoe.traffictracker.hateoas;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.mapper.MonthMapper;
import net.joedoe.traffictracker.model.Day;
import org.junit.Test;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class MonthAssemblerTest {
    private final MonthAssembler assembler = new MonthAssembler();
    private final LocalDate startDate = LocalDate.now().withDayOfMonth(1);
    private final MonthDto month = MonthMapper.toDto(startDate, DaysInit.createDays(LocalDate.now().getDayOfMonth() - 1));

    @Test
    public void toModel() {
        EntityModel<MonthDto> monthDto = assembler.toModel(month);

        assertEquals("/api/months/" + startDate.getYear() + "/" + startDate.getMonthValue(), monthDto.getRequiredLink("self").getHref());
        LocalDate tmp = startDate.minusMonths(1);
        assertEquals("/api/months/" + tmp.getYear() + "/" + tmp.getMonthValue(), monthDto.getRequiredLink("prev_month").getHref());
        assertFalse(monthDto.hasLink("next_month"));
        assertEquals("/api/weeks/" + startDate, monthDto.getRequiredLink("weeks").getHref());
        assertEquals("/api/years/" + startDate.getYear(), monthDto.getRequiredLink("year").getHref());
    }
}