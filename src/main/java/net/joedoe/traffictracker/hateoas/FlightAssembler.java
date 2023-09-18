package net.joedoe.traffictracker.hateoas;

import jakarta.annotation.Nonnull;
import net.joedoe.traffictracker.controller.DayController;
import net.joedoe.traffictracker.controller.FlightController;
import net.joedoe.traffictracker.controller.PlaneController;
import net.joedoe.traffictracker.dto.FlightDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FlightAssembler implements RepresentationModelAssembler<FlightDto, EntityModel<FlightDto>> {
    @Nonnull
    @Override
    public EntityModel<FlightDto> toModel(@Nonnull FlightDto flight) {
        EntityModel<FlightDto> model = EntityModel.of(flight);
        model.add(linkTo(methodOn(PlaneController.class).getFlightsByPlaneIcao(flight.getIcao_24(), null, null)).withRel("icao_24"));
        model.add(linkTo(methodOn(DayController.class).getDayByDate(flight.getDate())).withRel("day"));
        if (flight.isPhoto())
            model.add(linkTo(methodOn(FlightController.class).getPhoto(flight.getId())).withRel("photo_url"));
        return model;
    }
}
