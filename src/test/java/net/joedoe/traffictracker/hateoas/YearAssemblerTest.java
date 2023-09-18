package net.joedoe.traffictracker.hateoas;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.mapper.YearMapper;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

public class YearAssemblerTest {
    private final YearAssembler assembler = new YearAssembler();
    private final LocalDate startDate = LocalDate.now().withDayOfMonth(1).withMonth(1);

    @Test
    public void toModel() {
        YearDto year = YearMapper.toDto(startDate, DaysInitTest.createDays(LocalDate.now().getDayOfYear() - 1), true, false);

        EntityModel<YearDto> yearDto = assembler.toModel(year);

        assertEquals("/api/years/" + startDate.getYear(), yearDto.getRequiredLink("self").getHref());
        assertEquals("/api/years/" + (startDate.getYear() - 1), yearDto.getRequiredLink("prev_year").getHref());
        assertFalse(yearDto.hasLink("next_year"));
        assertEquals("/api/months/" + startDate.getYear() + "/" + startDate.getMonthValue(), yearDto.getRequiredLink("months").getHref());
    }
}
