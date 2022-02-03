package net.joedoe.traffictracker.service;

import graphql.GraphQLException;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.FlightMapper;
import net.joedoe.traffictracker.mapper.PageMapper;
import net.joedoe.traffictracker.model.Airport;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.repo.FlightRepository;
import net.joedoe.traffictracker.util.PropertiesHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Service
public class FlightService {
    private final FlightRepository repository;
    private final AirportService airportService;
    private final PageMapper<Flight> pageMapper = new PageMapper<>();
    private int flightsSavedInDays;

    public FlightService(FlightRepository repository, AirportService airportService) {
        this.repository = repository;
        this.airportService = airportService;
        try {
            Properties prop = PropertiesHandler.getProperties("src/main/resources/flights-db.properties");
            this.flightsSavedInDays = Integer.parseInt(prop.getProperty("flights.saved.in.days"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDeparture(String icaoAirport, Flight flight) {
        Airport airport = airportService.findOrCreate(icaoAirport);
        flight.setDeparture(airport);
        repository.save(flight);
    }

    public List<Flight> getByDate(LocalDate date) {
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        return repository.getFlightsByDateTimeBetweenOrderByDateTimeDesc(time, time.plusDays(1)).orElse(null);
    }

    @Transactional
    public void delete(){
        repository.deleteByDateTimeIsBefore(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(flightsSavedInDays + 1));
    }

    // Rest

    public Page<FlightDto> getByDate(LocalDate date, Pageable pageable) {
        if (date.isBefore(LocalDate.now().minusDays(flightsSavedInDays))) {
            throw new NotFoundException("Only flights for the last " + flightsSavedInDays + " days");
        }
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        Optional<Page<Flight>> page = repository.getFlightsByDateTimeBetweenOrderByDateTimeDesc(time, time.plusDays(1), pageable);
        if (!page.isPresent()) {
            throw new NotFoundException("Could not find flights for date " + date);
        }
        return page.get().map(FlightMapper::toDto);
    }

    public Page<FlightDto> getByPlaneIcao(String icao, Pageable pageable) {
        Optional<Page<Flight>> page = repository.findByPlaneIcao(icao, pageable);
        if (!page.isPresent()) {
            throw new NotFoundException("Could not find flights by plane with icao " + icao);
        }
        return page.get().map(FlightMapper::toDto);
    }

    public void savePhoto(Long id, MultipartFile file) throws IOException {
        Flight flight = repository.findById(id).orElse(null);
        if (flight == null) {
            log.info("Could not find flight with id " + id);
            throw new NotFoundException("Could not find flight with id " + id);
        }
        Byte[] bytes = new Byte[file.getBytes().length]; // hibernate prefers obj to primitives
        int i = 0;
        for (byte b : file.getBytes()) bytes[i++] = b;
        flight.setPhoto(bytes);
        repository.save(flight);
        log.info("Photo added: " + flight);
    }

    public byte[] loadPhoto(Long id) {
        Flight flight = repository.findById(id).orElse(null);
        if (flight == null)
            throw new NotFoundException("Could not find flight with id " + id);
        if (flight.getPhoto() == null)
            throw new NotFoundException("Could not find photo of flight with id " + id);
        byte[] bytes = new byte[flight.getPhoto().length];
        int i = 0;
        for (Byte b : flight.getPhoto()) bytes[i++] = b; // auto unboxing
        return bytes;
    }

    // GraphQL

    public PageDto<Flight> findByAirlineIcao(String icao, PageRequestDto req) {
        Optional<Page<Flight>> flights = repository.findByAirlineIcao(icao, PageRequest.of(req.getPage(), req.getSize()));
        if (!flights.isPresent()) {
            throw new GraphQLException("Could not find flights by airline with icao " + icao);
        }
        return pageMapper.toDto(flights.get());
    }

    public PageDto<Flight> findByAirportIcao(String icao, PageRequestDto req) {
        Optional<Page<Flight>> flights = repository.findByAirportIcao(icao, PageRequest.of(req.getPage(), req.getSize()));
        if (!flights.isPresent()) {
            throw new GraphQLException("Could not find flights from airport with icao " + icao);
        }
        return pageMapper.toDto(flights.get());
    }

    public PageDto<Flight> findByPlaneIcao(String icao, PageRequestDto req) {
        Optional<Page<Flight>> flights = repository.findByPlaneIcao(icao, PageRequest.of(req.getPage(), req.getSize()));
        if (!flights.isPresent()) {
            throw new GraphQLException("Could not find flights by plane with icao " + icao);
        }
        return pageMapper.toDto(flights.get());
    }

    public PageDto<Flight> findByDate(LocalDate date, PageRequestDto req) {
        if (date.isBefore(LocalDate.now().minusDays(flightsSavedInDays))) {
            throw new GraphQLException("Only flights for the last " + flightsSavedInDays + " days");
        }
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        Optional<Page<Flight>> flights = repository.getFlightsByDateTimeBetweenOrderByDateTimeDesc(time, time.plusDays(1), PageRequest.of(req.getPage(), req.getSize()));
        if (!flights.isPresent()) {
            throw new GraphQLException("Could not find flights for date " + date);
        }
        return pageMapper.toDto(flights.get());
    }
}
