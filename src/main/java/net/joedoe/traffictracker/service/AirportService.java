package net.joedoe.traffictracker.service;

import graphql.GraphQLException;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.mapper.PageMapper;
import net.joedoe.traffictracker.model.Airport;
import net.joedoe.traffictracker.model.Region;
import net.joedoe.traffictracker.repo.AirportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AirportService {
    private final AirportRepository repository;
    private final PageMapper<Airport> pageMapper = new PageMapper<>();


    public AirportService(AirportRepository repository) {
        this.repository = repository;
    }

    public Airport findOrCreate(String icao) {
        Optional<Airport> airport = repository.findByIcao(icao);
        return airport.orElseGet(() -> repository.save(new Airport(icao)));
    }

    // GraphQL

    public Airport findByIcao(String icao) {
        Optional<Airport> airport = repository.findByIcao(icao);
        if (!airport.isPresent()) {
            throw new GraphQLException("Could not find airport with icao " + icao);
        }
        return airport.get();
    }

    public PageDto<Airport> findAll(PageRequestDto req) {
        Optional<Page<Airport>> airports = repository.findAllWithPagination(PageRequest.of(req.getPage(), req.getSize()));
        if (!airports.isPresent()) {
            throw new GraphQLException("Could not find airports");
        }
        return pageMapper.toDto(airports.get());
    }

    public PageDto<Airport> findAllByRegion(Region region, PageRequestDto req) {
        Optional<Page<Airport>> airports = repository.findAllByRegion(region, PageRequest.of(req.getPage(), req.getSize()));
        if (!airports.isPresent()) {
            throw new GraphQLException("Could not find airports in region " + region);
        }
        return pageMapper.toDto(airports.get());
    }
}
