package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.PlaneClient;
import net.joedoe.traffictracker.exception.ResourceNotFoundException;
import net.joedoe.traffictracker.model.MapData;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.repo.PlaneRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class PlaneService {
    private PlaneRepository repository;
    private PlaneClient client;

    public PlaneService(PlaneRepository repository, PlaneClient client) {
        this.repository = repository;
        this.client = client;
    }

    public Plane getPlaneById(Long id) {
        return repository.getPlaneById(id);
    }

    public Page<Plane> getPlanesByDate(LocalDate date, Pageable pageable) {
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        return repository.getPlanesByDateBetweenOrderByDateDesc(time, time.plusDays(1), pageable)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Page<Plane> getPlanesByIcao(String icao, Pageable pageable) {
        return repository.getPlanesByIcaoOrderByDateDesc(icao, pageable)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Plane> getPlanesWithMaxAltitude() {
        return repository.getPlanesWithMaxAltitude()
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Plane> getPlanesWithMaxSpeed() {
        return repository.getPlanesWithMaxSpeed()
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Plane> getPlanesWithMinAltitude() {
        return repository.getPlanesWithMinAltitude()
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Plane> getPlanesWithMinSpeed() {
        return repository.getPlanesWithMinSpeed()
                .orElseThrow(ResourceNotFoundException::new);
    }

    public MapData getCurrentPlanes() {
        return client.getCurrentPlanes();
    }
}
