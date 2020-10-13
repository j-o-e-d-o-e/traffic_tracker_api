package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.bootstrap.DaysInit;
import net.joedoe.traffictracker.bootstrap.PlanesInit;
import net.joedoe.traffictracker.dto.PlaneDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Plane;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class PlaneMapperTest {
    private final PlaneMapper mapper = new PlaneMapper();
    private Plane plane;

    @Before
    public void setUp() {
        Day day = DaysInit.createDay(LocalDate.now());
        plane = PlanesInit.createPlane(day);
    }

    @Test
    public void toResource() {
        PlaneDto planeDto = mapper.toResource(plane);

        assertEquals("/planes/id/" + plane.getId(), planeDto.getLink("self").getHref());
        assertEquals("/planes/icao24/" + plane.getIcao(), planeDto.getLink("icao_24").getHref());
        assertEquals("/planes/day/id/" + plane.getDay().getId(), planeDto.getLink("day").getHref());
    }

    @Test
    public void planeToPlaneDto() {
        PlaneDto planeDto = mapper.toResource(plane);

        assertEquals(plane.getIcao(), planeDto.getIcao_24());
        assertEquals(plane.getDate(), planeDto.getDate_time());
        assertEquals(plane.getAltitude(), planeDto.getAltitude());
        assertEquals(plane.getSpeed(), planeDto.getSpeed());
    }
}