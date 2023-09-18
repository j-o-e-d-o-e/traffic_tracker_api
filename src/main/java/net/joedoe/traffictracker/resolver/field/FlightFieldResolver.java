package net.joedoe.traffictracker.resolver.field;

import net.joedoe.traffictracker.controller.FlightController;
import net.joedoe.traffictracker.model.Flight;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
public class FlightFieldResolver  {

    @SchemaMapping
    public LocalDate date(Flight flight) {
        return flight.getDateTime().toLocalDate();
    }

    @SchemaMapping
    public OffsetTime time(Flight flight) {
        return flight.getDateTime().toLocalTime().atOffset(ZoneOffset.UTC);
    }

    @SchemaMapping
    public String photo(Flight flight){
        if (flight.getPhoto() == null) return null;
        return linkTo(methodOn(FlightController.class).getPhoto(flight.getId())).withRel("photo_url").getHref();
    }
}
