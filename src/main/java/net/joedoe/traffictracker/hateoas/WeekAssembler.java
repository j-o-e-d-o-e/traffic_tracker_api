package net.joedoe.traffictracker.hateoas;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.DayController;
import net.joedoe.traffictracker.controller.MonthController;
import net.joedoe.traffictracker.controller.WeekController;
import net.joedoe.traffictracker.dto.WeekDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Component
public class WeekAssembler implements RepresentationModelAssembler<WeekDto, EntityModel<WeekDto>> {

    @NotNull
    @Override
    public EntityModel<WeekDto> toModel(@NotNull WeekDto week) {
        LocalDate date = week.getStart_date();
        EntityModel<WeekDto> model = EntityModel.of(week);
        model.add(linkTo(methodOn(WeekController.class).getWeekByDate(date)).withSelfRel());
        if (week.isPrev())
            model.add(linkTo(methodOn(WeekController.class).getWeekByDate(date.minusWeeks(1))).withRel("prev_week"));
        if (week.isNext())
            model.add(linkTo(methodOn(WeekController.class).getWeekByDate(date.plusWeeks(1))).withRel("next_week"));
        model.add(linkTo(methodOn(DayController.class).getDayByDate(week.getFirst_day())).withRel("days"));
        model.add(linkTo(methodOn(MonthController.class).getMonthByDate(date.getYear(), date.getMonthValue())).withRel("month"));
        return model;
    }
}
