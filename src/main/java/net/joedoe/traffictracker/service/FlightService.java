package net.joedoe.traffictracker.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.DepartureClient;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.FlightMapper;
import net.joedoe.traffictracker.mapper.PageMapper;
import net.joedoe.traffictracker.model.Airport;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.Photo;
import net.joedoe.traffictracker.repo.FlightRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class FlightService {
    private final FlightRepository repository;
    private final AirportService airportService;
    private final PageMapper<Flight> pageMapper = new PageMapper<>();
    private final int flightsSavedInDays = 7;
    private final String timezone;

    public FlightService(FlightRepository repository, AirportService airportService, @Value("${locale.timezone}") String timezone) {
        this.repository = repository;
        this.airportService = airportService;
        this.timezone = timezone;
    }


    public boolean setDepartures(List<DepartureClient.Departure> departures, LocalDate date) {
        List<Flight> flights = getFlightsByDate(date);
        log.info("Total fetched from api: " + departures.size());
        log.info("Total fetched from db: " + flights.size());
        float departsPerFlight = departures.size() / (float) flights.size();
        if (departsPerFlight < 0.5){
            log.info("Not enough departures. Departures/flight: " + departsPerFlight);
            return false;
        }
        for (DepartureClient.Departure departure : departures) {
            if (departure.estDepartureAirport == null) continue;
            LocalDateTime lastSeen = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(departure.lastSeen * 1000),
                    TimeZone.getTimeZone(timezone).toZoneId());
            for (Flight f : flights) {
                if (!(f.getDeparture() == null || f.getDeparture().getIcao() == null)) continue;
                if (f.getPlane() == null || f.getPlane().getIcao() == null) continue;
                if (!departure.icao24.equals(f.getPlane().getIcao())) continue;
                if (lastSeen.isBefore(f.getDateTime().plusMinutes(30)) && lastSeen.isAfter(f.getDateTime().minusMinutes(30))) {
                    setDeparture(departure.estDepartureAirport, f);
                }
            }
        }
        return true;
    }

//    public void setDepartures(List<DepartureClient.Departure> departures, LocalDate date) {
//        log.info("Total fetched from api: " + departures.size());
//        List<Flight> flights = getFlightsByDate(date);
//        ListIterator<Flight> flightIter = flights.listIterator(flights.size());
//        log.info("Total fetched from db: " + flights.size());
//        if (!flightIter.hasPrevious()) return;
//        Flight flight = flightIter.previous();
//        int count = 0;
//        departures.sort(Comparator.comparing(d -> d.lastSeen));
//        for (DepartureClient.Departure departure : departures) {
//            if (!(flight.getDeparture() == null || flight.getDeparture().getIcao() == null)
//                    || flight.getPlane() == null || flight.getPlane().getIcao() == null) {
//                if (flightIter.hasPrevious()) {
//                    flight = flightIter.previous();
//                    continue;
//                } else break;
//            }
//            LocalDateTime lastSeen = LocalDateTime.ofInstant(
//                    Instant.ofEpochMilli(departure.lastSeen * 1000),
//                    TimeZone.getTimeZone(timezone).toZoneId());
//            if (lastSeen.isBefore(flight.getDateTime().minusMinutes(30))
//                    || !departure.icao24.equals(flight.getPlane().getIcao())) continue;
//            if (departure.estDepartureAirport == null) {
//                if (lastSeen.isBefore(flight.getDateTime())) continue;
//                if (flightIter.hasPrevious()) {
//                    flight = flightIter.previous();
//                    continue;
//                } else break;
//            }
//            count++;
////            setDeparture(departure.estDepartureAirport, flight);
//            if (flightIter.hasPrevious()) flight = flightIter.previous();
//            else break;
//        }
//        System.out.println(count);
//    }

    public void setDeparture(String icaoAirport, Flight flight) {
        Airport airport = airportService.findOrCreate(icaoAirport);
        flight.setDeparture(airport);
        repository.save(flight);
    }

    public List<Flight> getFlightsByDate(LocalDate date) {
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        return repository.getFlightsByDateTimeBetweenOrderByDateTimeDesc(time, time.plusDays(1)).orElse(null);
    }

    @Transactional
    public void delete() {
        repository.deleteByDateTimeIsBefore(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(flightsSavedInDays));
    }

    // Rest

    public Page<FlightDto> getFlightsForLatestDay(Pageable pageable) {
        Optional<Flight> latest = repository.findDistinctFirstByOrderByDateTimeDesc();
        if (latest.isEmpty()) throw new NotFoundException("No flights found");
        LocalDateTime end = latest.get().getDateTime().plusMinutes(1);
        LocalDateTime start = LocalDateTime.of(LocalDate.of(end.getYear(), end.getMonth(), end.getDayOfMonth()).minusDays(1), LocalTime.of(23, 59, 59));
        Optional<Page<Flight>> page = repository.getFlightsByDateTimeBetweenOrderByDateTimeDesc(start, end, pageable);
        if (page.isEmpty()) throw new NotFoundException("Could not find latest flights for date");
        return page.get().map(FlightMapper::toDto);
    }

    public Page<FlightDto> getFlightsByDate(LocalDate date, Pageable pageable) {
        if (date.isBefore(LocalDate.now().minusDays(flightsSavedInDays)))
            throw new NotFoundException("Only flights for the last " + flightsSavedInDays + " days");
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        Optional<Page<Flight>> page = repository.getFlightsByDateTimeBetweenOrderByDateTimeDesc(time, time.plusDays(1), pageable);
        if (page.isEmpty()) throw new NotFoundException("Could not find flights for date " + date);
        return page.get().map(FlightMapper::toDto);
    }

    public Page<FlightDto> getByPlaneIcao(String icao, Pageable pageable) {
        Optional<Page<Flight>> page = repository.findByPlaneIcao(icao, pageable);
        if (page.isEmpty()) throw new NotFoundException("Could not find flights by plane with icao " + icao);
        return page.get().map(FlightMapper::toDto);
    }

    public void savePhoto(Long id, MultipartFile file) throws IOException {
        Flight flight = repository.findById(id).orElse(null);
        if (flight == null) throw new NotFoundException("Could not find flight with id " + id);
        flight.setPhoto(file.getBytes());
        repository.save(flight);
        log.info("Photo added for flight with id " + id);
    }

    public byte[] loadPhoto(Long id) {
        Photo photo = repository.findPhotoByFlightId(id).orElse(null);
        if (photo == null) throw new NotFoundException("Could not find photo for flight with id " + id);
        return photo.getArr();
    }

    // GraphQL

    public PageDto<Flight> findByAirlineIcao(String icao, PageRequestDto req) {
        Optional<Page<Flight>> flights = repository.findByAirlineIcao(icao, PageRequest.of(req.getPage(), req.getSize()));
        if (flights.isEmpty()) throw new NotFoundException("Could not find flights by airline with icao " + icao);
        return pageMapper.toDto(flights.get());
    }

    public PageDto<Flight> findByAirportIcao(String icao, PageRequestDto req) {
        Optional<Page<Flight>> flights = repository.findByAirportIcao(icao, PageRequest.of(req.getPage(), req.getSize()));
        if (flights.isEmpty()) throw new NotFoundException("Could not find flights from airport with icao " + icao);
        return pageMapper.toDto(flights.get());
    }

    public PageDto<Flight> findByPlaneIcao(String icao, PageRequestDto req) {
        Optional<Page<Flight>> flights = repository.findByPlaneIcao(icao, PageRequest.of(req.getPage(), req.getSize()));
        if (flights.isEmpty()) throw new NotFoundException("Could not find flights by plane with icao " + icao);
        return pageMapper.toDto(flights.get());
    }

    public PageDto<Flight> findByDate(LocalDate date, PageRequestDto req) {
        if (date.isBefore(LocalDate.of(2021, 9, 9).minusDays(flightsSavedInDays)))
            throw new NotFoundException("Only flights for the last " + flightsSavedInDays + " days");
        LocalDateTime time = LocalDateTime.of(date.minusDays(1), LocalTime.of(23, 59, 59));
        Optional<Page<Flight>> flights = repository.getFlightsByDateTimeBetweenOrderByDateTimeDesc(time, time.plusDays(1), PageRequest.of(req.getPage(), req.getSize()));
        if (flights.isEmpty()) throw new NotFoundException("Could not find flights for date " + date);
        return pageMapper.toDto(flights.get());
    }
}
