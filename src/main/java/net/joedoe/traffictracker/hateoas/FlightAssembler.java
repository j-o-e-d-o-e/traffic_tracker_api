package net.joedoe.traffictracker.hateoas;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.controller.DayController;
import net.joedoe.traffictracker.controller.FlightController;
import net.joedoe.traffictracker.dto.FlightDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Component
public class FlightAssembler implements RepresentationModelAssembler<FlightDto, EntityModel<FlightDto>> {

    @NotNull
    @Override
    public EntityModel<FlightDto> toModel(@NotNull FlightDto flight) {
        EntityModel<FlightDto> model = EntityModel.of(flight);
        model.add(linkTo(methodOn(FlightController.class).getFlightById(flight.getId())).withSelfRel());
        model.add(linkTo(methodOn(FlightController.class).getFlightsByIcao24(flight.getIcao_24(), null, null)).withRel("icao_24"));
        model.add(linkTo(methodOn(DayController.class).getDayByDate(flight.getDate())).withRel("day"));
        return model;
    }
}
