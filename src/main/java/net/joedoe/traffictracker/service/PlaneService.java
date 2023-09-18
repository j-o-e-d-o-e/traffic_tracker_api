package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.PageMapper;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.repo.PlaneRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaneService {
    private final PlaneRepository repository;
    private final PageMapper<Plane> pageMapper = new PageMapper<>();

    public PlaneService(PlaneRepository repository) {
        this.repository = repository;
    }

    public Plane findOrCreate(String icao) {
        Optional<Plane> plane = repository.findByIcao(icao);
        return plane.orElseGet(() -> repository.save(new Plane(icao)));
    }

    public void delete() {
        repository.findAllJoinFetchFlights().stream()
                .filter(plane -> plane.getFlights().isEmpty()).forEach(repository::delete);
    }

    // GraphQL

    public Plane findByIcao(String icao) {
        Optional<Plane> plane = repository.findByIcao(icao);
        if (plane.isEmpty()) throw new NotFoundException("Could not find plane with icao " + icao);
        return plane.get();
    }

    public PageDto<Plane> findAll(PageRequestDto req) {
        Optional<Page<Plane>> planes = repository.findAllWithPagination(PageRequest.of(req.getPage(), req.getSize()));
        if (planes.isEmpty()) throw new NotFoundException("Could not find planes");
        return pageMapper.toDto(planes.get());
    }
}
