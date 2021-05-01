package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.FlightClient;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.FlightMapper;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.MapData;
import net.joedoe.traffictracker.repo.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlightService {
    private final FlightRepository repository;
    private final FlightClient client;

    public FlightService(FlightRepository repository, FlightClient client) {
        this.repository = repository;
        this.client = client;
    }

    public FlightDto getFlightById(Long id) {
        Optional<Flight> flight = repository.getFlightById(id);
        if (!flight.isPresent()) {
            throw new NotFoundException("Could not find flight " + id);
        }
        return FlightMapper.toDto(flight.get());
    }

    public Page<FlightDto> getFlightsByDate(LocalDate date, Pageable pageable) {
        if (date.isBefore(LocalDate.now().minusDays(30))) {
            throw new NotFoundException("Only flights for the last 30 days");
        }
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        Optional<Page<Flight>> page = repository.getFlightsByDateBetweenOrderByDateDesc(time, time.plusDays(1), pageable);
        if (!page.isPresent()) {
            throw new NotFoundException("Could not find flights for " + date);
        }
        return page.get().map(FlightMapper::toDto);
    }

    public Page<FlightDto> getFlightsByIcao(String icao, Pageable pageable) {
        Optional<Page<Flight>> page = repository.getFlightsByIcaoOrderByDateDesc(icao, pageable);
        if (!page.isPresent()) {
            throw new NotFoundException("Could not find flights with " + icao);
        }
        return page.get().map(FlightMapper::toDto);
    }

    public List<FlightDto> getFlightsDtoListByDate(LocalDate date) {
        return getFlightsListByDate(date).stream().map(FlightMapper::toDto).collect(Collectors.toList());
    }

    public List<Flight> getFlightsListByDate(LocalDate date) {
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        Optional<List<Flight>> flights = repository.getFlightsByDateBetweenOrderByDateDesc(time, time.plusDays(1));
        if (!flights.isPresent()) {
            throw new NotFoundException("Could not find flights for " + date);
        }
        return flights.get();
    }

    public void save(Flight flight) {
        repository.save(flight);
    }

    public MapData getCurrentFlights() {
        return client.getCurrentFlights();
    }
}
