package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.controller.PlaneController;
import net.joedoe.traffictracker.controller.DayController;
import net.joedoe.traffictracker.dto.PlaneDto;
import net.joedoe.traffictracker.model.Plane;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class PlaneMapper extends ResourceAssemblerSupport<Plane, PlaneDto> {

    public PlaneMapper() {
        super(PlaneController.class, PlaneDto.class);
    }

    @Override
    public PlaneDto toResource(Plane plane) {
        if (plane == null)
            return null;
        PlaneDto planeDto = planeToPlaneDto(plane);

        planeDto.add(linkTo(methodOn(PlaneController.class).getPlaneById(plane.getId())).withSelfRel());
        planeDto.add(linkTo(methodOn(PlaneController.class).getPlanesByIcao24(plane.getIcao(), null, null)).withRel("icao_24"));
        planeDto.add(linkTo(methodOn(DayController.class).getDayById(plane.getDay().getId())).withRel("day"));
        return planeDto;
    }

    private PlaneDto planeToPlaneDto(Plane plane) {
        LocalDate date = LocalDate.from(plane.getDate());
        return new PlaneDto(plane.getIcao(), plane.getDate(), date, plane.getAltitude(), plane.getSpeed(),
                plane.getDepartureAirport(), plane.getDepartureAirportName(), plane.getAirline(), plane.getAirlineName());
    }
}
