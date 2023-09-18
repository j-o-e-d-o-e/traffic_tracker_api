package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.PageMapper;
import net.joedoe.traffictracker.model.Airline;
import net.joedoe.traffictracker.repo.AirlineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirlineService {
    private final AirlineRepository repository;
    private final PageMapper<Airline> pageMapper = new PageMapper<>();

    public AirlineService(AirlineRepository repository) {
        this.repository = repository;
    }

    public Airline findOrCreate(String icao) {
        Optional<Airline> airline = repository.findByIcao(icao);
        return airline.orElseGet(() -> repository.save(new Airline(icao)));
    }

    // GraphQL

    public Airline findByIcao(String icao) {
        Optional<Airline> airline = repository.findByIcao(icao);
        if (airline.isEmpty()) throw new NotFoundException("Could not find airline with icao " + icao);
        return airline.get();
    }

    public PageDto<Airline> findAll(PageRequestDto req) {
        Optional<Page<Airline>> airlines = repository.findAllWithPagination(PageRequest.of(req.getPage(), req.getSize()));
        if (airlines.isEmpty()) throw new NotFoundException("Could not find airlines");
        return pageMapper.toDto(airlines.get());
    }
}
