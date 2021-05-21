package net.joedoe.traffictracker.hateoas;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.DayController;
import net.joedoe.traffictracker.controller.FlightController;
import net.joedoe.traffictracker.controller.WeekController;
import net.joedoe.traffictracker.dto.DayDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Component
public class DayAssembler implements RepresentationModelAssembler<DayDto, EntityModel<DayDto>> {

    @NotNull
    @Override
    public EntityModel<DayDto> toModel(@NotNull DayDto day) {
        LocalDate date = day.getDate();
        EntityModel<DayDto> model = EntityModel.of(day);
        model.add(linkTo(methodOn(DayController.class).getDayByDate(day.getDate())).withSelfRel());
        if (day.isPrev())
            model.add(linkTo(methodOn(DayController.class).getDayByDate(date.minusDays(1))).withRel("prev_day"));
        if (day.isNext())
            model.add(linkTo(methodOn(DayController.class).getDayByDate(date.plusDays(1))).withRel("next_day"));
        if (day.isFlights())
            model.add(linkTo(methodOn(FlightController.class).getFlightsByDate(date, null, null)).withRel("flights"));
        model.add(linkTo(methodOn(WeekController.class).getWeekByDate(date.with(DayOfWeek.MONDAY))).withRel("week"));
        return model;
    }
}
