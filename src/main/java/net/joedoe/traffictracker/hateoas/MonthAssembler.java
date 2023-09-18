package net.joedoe.traffictracker.hateoas;

import jakarta.annotation.Nonnull;
import net.joedoe.traffictracker.controller.MonthController;
import net.joedoe.traffictracker.controller.WeekController;
import net.joedoe.traffictracker.controller.YearController;
import net.joedoe.traffictracker.dto.MonthDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MonthAssembler implements RepresentationModelAssembler<MonthDto, EntityModel<MonthDto>> {
    @Nonnull
    @Override
    public EntityModel<MonthDto> toModel(@Nonnull MonthDto month) {
        EntityModel<MonthDto> model = EntityModel.of(month);
        model.add(linkTo(methodOn(MonthController.class).getMonthByDate(month.getYear(), month.getMonth())).withSelfRel());
        if (month.isPrev()) {
            LocalDate date = LocalDate.of(month.getYear(), month.getMonth(), 1).minusMonths(1);
            model.add(linkTo(methodOn(MonthController.class).getMonthByDate(date.getYear(), date.getMonthValue())).withRel("prev_month"));
        }
        if (month.isNext()) {
            LocalDate date = LocalDate.of(month.getYear(), month.getMonth(), 1).plusMonths(1);
            model.add(linkTo(methodOn(MonthController.class).getMonthByDate(date.getYear(), date.getMonthValue())).withRel("next_month"));
        }
        model.add(linkTo(methodOn(WeekController.class).getWeekByDate(month.getFirst_week())).withRel("weeks"));
        model.add(linkTo(methodOn(YearController.class).getYearByDate(month.getYear())).withRel("year"));
        return model;
    }
}
