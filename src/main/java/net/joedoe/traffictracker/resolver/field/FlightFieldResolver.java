package net.joedoe.traffictracker.resolver.field;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.Flight;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class FlightFieldResolver implements GraphQLResolver<Flight> {

    public LocalDate date(Flight flight) {
        return flight.getDateTime().toLocalDate();
    }

    public OffsetTime time(Flight flight) {
        return flight.getDateTime().toLocalTime().atOffset(ZoneOffset.UTC);
    }
}
