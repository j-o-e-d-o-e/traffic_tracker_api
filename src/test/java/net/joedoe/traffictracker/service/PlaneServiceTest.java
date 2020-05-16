package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.PlanesInitTest;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.repo.PlaneRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PlaneServiceTest {
    @InjectMocks
    private PlaneService service;
    @Mock
    private PlaneRepository repository;
    private LocalDate date = LocalDate.now();
    private List<Plane> planes = PlanesInitTest.createPlanes(date);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getPlaneById() {
        Plane plane = planes.get(0);

        when(repository.getPlaneById(plane.getId())).thenReturn(plane);
        Plane plane1 = service.getPlaneById(plane.getId());

        assertEquals(plane.getDate(), plane1.getDate());
    }

    @Test
    public void getPlanesByDate() {
        Page<Plane> page = new PageImpl<>(planes, PageRequest.of(0, 20), 1);

        when(repository.getPlanesByDateBetweenOrderByDateDesc(any(), any(), any())).thenReturn(Optional.of(page));
        Page<Plane> planes = service.getPlanesByDate(date, Pageable.unpaged());

        assertEquals(page.getSize(), planes.getSize());
    }

    @Test
    public void getPlanesByIcao() {
        Page<Plane> page = new PageImpl<>(planes, PageRequest.of(0, 20), 1);

        when(repository.getPlanesByIcaoOrderByDateDesc(anyString(), any())).thenReturn(Optional.of(page));
        Page<Plane> planes = service.getPlanesByIcao("", null);

        assertEquals(page.getSize(), planes.getSize());
    }

    @Test
    public void getPlanesWithMaxAltitude() {
        Plane plane = Collections.max(planes, Comparator.comparing(Plane::getAltitude));

        when(repository.getPlanesWithMaxAltitude()).thenReturn(Optional.of(Collections.singletonList(plane)));
        List<Plane> planes = service.getPlanesWithMaxAltitude();

        Plane plane1 = planes.get(0);
        assertEquals(plane.getIcao(), plane1.getIcao());
        assertEquals(plane.getDate(), plane1.getDate());
        assertEquals(plane.getAltitude(), plane1.getAltitude());
        assertEquals(plane.getSpeed(), plane1.getSpeed());
    }

    @Test
    public void getPlanesWithMaxSpeed() {
        Plane plane = Collections.max(planes, Comparator.comparing(Plane::getSpeed));

        when(repository.getPlanesWithMaxSpeed()).thenReturn(Optional.of(Collections.singletonList(plane)));
        List<Plane> planes = service.getPlanesWithMaxSpeed();

        Plane plane1 = planes.get(0);
        assertEquals(plane.getIcao(), plane1.getIcao());
        assertEquals(plane.getDate(), plane1.getDate());
        assertEquals(plane.getAltitude(), plane1.getAltitude());
        assertEquals(plane.getSpeed(), plane1.getSpeed());
    }

    @Test
    public void getPlanesWithMinAltitude() {
        Plane plane = Collections.min(planes, Comparator.comparing(Plane::getAltitude));

        when(repository.getPlanesWithMinAltitude()).thenReturn(Optional.of(Collections.singletonList(plane)));
        List<Plane> planes = service.getPlanesWithMinAltitude();

        Plane plane1 = planes.get(0);
        assertEquals(plane.getIcao(), plane1.getIcao());
        assertEquals(plane.getDate(), plane1.getDate());
        assertEquals(plane.getAltitude(), plane1.getAltitude());
        assertEquals(plane.getSpeed(), plane1.getSpeed());
    }

    @Test
    public void getPlanesWithMinSpeed() {
        Plane plane = Collections.min(planes, Comparator.comparing(Plane::getSpeed));

        when(repository.getPlanesWithMinSpeed()).thenReturn(Optional.of(Collections.singletonList(plane)));
        List<Plane> planes = service.getPlanesWithMinSpeed();

        Plane plane1 = planes.get(0);
        assertEquals(plane.getIcao(), plane1.getIcao());
        assertEquals(plane.getDate(), plane1.getDate());
        assertEquals(plane.getAltitude(), plane1.getAltitude());
        assertEquals(plane.getSpeed(), plane1.getSpeed());
    }
}