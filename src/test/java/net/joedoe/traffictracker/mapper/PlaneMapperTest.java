package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.bootstrap.PlanesInitTest;
import net.joedoe.traffictracker.dto.PlaneDto;
import net.joedoe.traffictracker.model.Plane;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class PlaneMapperTest {
    private PlaneMapper mapper = new PlaneMapper();
    private Plane plane = PlanesInitTest.createPlanes(LocalDate.now()).get(0);

    @Test
    public void planeToResource() {
        PlaneDto planeDto = mapper.toResource(plane);

        assertEquals(plane.getIcao(), planeDto.getIcao_24());
        assertEquals(plane.getDate(), planeDto.getDate_time());
        assertEquals(plane.getAltitude(), planeDto.getAltitude());
        assertEquals(plane.getSpeed(), planeDto.getSpeed());
    }
}