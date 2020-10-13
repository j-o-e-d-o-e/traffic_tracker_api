package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.PlanesInit;
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
    private final List<Plane> planes = PlanesInit.createPlanes();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getPlaneById() {
        Plane exp = planes.get(0);

        when(repository.getPlaneById(exp.getId())).thenReturn(Optional.of(exp));
        Plane act = service.getPlaneById(exp.getId());

        assertEquals(exp.getDate(), act.getDate());
    }

    @Test
    public void getPlanesByDate() {
        Page<Plane> exp = new PageImpl<>(planes, PageRequest.of(0, 20), 1);

        when(repository.getPlanesByDateBetweenOrderByDateDesc(any(), any(), any())).thenReturn(Optional.of(exp));
        Page<Plane> act = service.getPlanesByDate(LocalDate.now(), Pageable.unpaged());

        assertEquals(exp.getSize(), act.getSize());
    }

    @Test
    public void getPlanesByIcao() {
        Page<Plane> exp = new PageImpl<>(planes, PageRequest.of(0, 20), 1);

        when(repository.getPlanesByIcaoOrderByDateDesc(anyString(), any())).thenReturn(Optional.of(exp));
        Page<Plane> act = service.getPlanesByIcao("", null);

        assertEquals(exp.getSize(), act.getSize());
    }
}