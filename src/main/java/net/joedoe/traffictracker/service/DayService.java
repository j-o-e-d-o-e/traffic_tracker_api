package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.client.DepartureClient;
import net.joedoe.traffictracker.dto.*;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.DayMapper;
import net.joedoe.traffictracker.mapper.FlightMapper;
import net.joedoe.traffictracker.mapper.PageMapper;
import net.joedoe.traffictracker.model.*;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DayService {
    private final DayRepository repository;
    private final DepartureClient departureClient;
    private final PlaneService planeService;
    private final AirlineService airlineService;
    private final FlightService flightService;
    private final WindService windService;
    private final PageMapper<Day> pageMapper = new PageMapper<>();

    public DayService(DayRepository repository, DepartureClient departureClient, PlaneService planeService,
                      AirlineService airlineService, FlightService flightService, WindService windService) {
        this.repository = repository;
        this.departureClient = departureClient;
        this.planeService = planeService;
        this.airlineService = airlineService;
        this.flightService = flightService;
        this.windService = windService;
    }

    public void addFlight(String icao, Flight flight) {
        Plane plane = planeService.findOrCreate(icao.toUpperCase());
        flight.setPlane(plane);
        Airline airline = airlineService.findOrCreate(flight.getCallsign().substring(0, 3));
        flight.setAirline(airline);
        Day currentDay = repository.findByDateJoinFetchFlights(flight.getDateTime().toLocalDate()).orElse(null);
        if (currentDay != null) {
            currentDay.addFlight(flight);
            repository.save(currentDay);
            log.info("New flight: " + flight);
        }
    }

    public List<FlightDto> addFlights(LocalDate date, List<FlightDto> flights) {
        if (flights.isEmpty()) log.info("Empty list");
        Day day = repository.findByDateJoinFetchFlights(date).orElse(null);
        if (day == null) {
            day = new Day(date);
            WindDay windDay = windService.getDayByDate(date);
            day.addWinds(windDay);
            windService.deleteAll();
        }
        List<Flight> dayFlights = day.getFlights();
        log.info("Total of " + day.getDate() + ": " + day.getTotal());
        int count = 0;
        for (FlightDto flight : flights) {
            if (dayFlights != null && !dayFlights.isEmpty()) {
                boolean exists = dayFlights.stream().anyMatch(f -> f.getCallsign().equals(flight.getCallsign()) && f.getDateTime().equals(flight.getDate_time()));
                if (exists) continue;
            }
            Flight newFlight = new Flight();
            if (flight.getCallsign().isEmpty()) newFlight.setCallsign("_NOCSGN");
            else newFlight.setCallsign(flight.getCallsign());
            newFlight.setDateTime(flight.getDate_time());
            newFlight.setAltitude(flight.getAltitude());
            newFlight.setSpeed(flight.getSpeed());
            Plane plane = planeService.findOrCreate(flight.getIcao_24());
            newFlight.setPlane(plane);
            if (newFlight.getCallsign().length() > 3) {
                Airline airline = airlineService.findOrCreate(newFlight.getCallsign().substring(0, 3));
                newFlight.setAirline(airline);
            }
            day.addFlight(newFlight);
            count++;
        }
        log.info("Flights added: " + count);
        log.info("Total of " + day.getDate() + ": " + day.getTotal());
        Day savedDay = repository.save(day);
        return savedDay.getFlights().stream().map(FlightMapper::toDto).collect(Collectors.toList());
    }

    // At 30 minutes past the hour, between 06:00 AM and 11:59 PM
    // 17 api-calls/day
    @Scheduled(cron = "0 30 6-23 * * *", zone = "${locale.timezone}")
    public void fetchWind() {
        windService.fetchWind();
    }

    // At 09:15 AM
    @Scheduled(cron = "0 15 9 * * *", zone = "${locale.timezone}")
    public void fetchDepartures() {
        List<DepartureClient.Departure> departures = departureClient.fetchDepartures();
        if (departures == null) return;
        LocalDate date = LocalDate.now().minusDays(1);
        flightService.setDepartures(departures, date);
        setDepartures(date);
        log.info("Departures fetched");
    }

    // At 06:30 AM
    @Scheduled(cron = "0 30 6 * * *", zone = "${locale.timezone}")
    public void free() {
        flightService.delete();     // delete flights before number of days = 'flightsSavedInDays'
        planeService.delete();      // ...  and planes without flights
        log.info("Freed space in db, deleted older data for flights and planes");
    }

    public void setDepartures(LocalDate date) {
        Day day = repository.findByDateJoinFetchFlights(date).orElse(null);
        if (day != null && !day.getFlights().isEmpty()) {
            day.setDepartures();
            repository.save(day);
            log.info("Departures set for day " + date);
        }
    }

    public List<Day> findAllJoinFetchFlights() {
        return repository.findAllJoinFetchFlights().orElse(null);
    }

    // Rest

    public DayDto getDayLatest() {
        Optional<Day> day = repository.findDistinctFirstByOrderByDateDesc();
        if (day.isEmpty()) throw new NotFoundException("Could not find any day");
        LocalDate date = day.get().getDate();
        return DayMapper.toDto(day.get(), hasNeighbour(date.minusDays(1)), false);
    }

    public DayDto getDayByDate(LocalDate date) {
        Optional<Day> day = repository.findByDate(date);
        if (day.isEmpty()) throw new NotFoundException("Could not find day " + date);
        return DayMapper.toDto(day.get(), hasNeighbour(date.minusDays(1)), hasNeighbour(date.plusDays(1)));
    }

    private boolean hasNeighbour(LocalDate date) {
        return repository.findByDate(date).isPresent();
    }

    public List<LocalDate> getDates() {
        return repository.findAllDates();
    }

    // GraphQL

    public Day findDayLatest() {
        Optional<Day> day = repository.findDistinctFirstByOrderByDateDesc();
        if (day.isEmpty()) throw new NotFoundException("Could not find any day");
        return day.get();
    }

    public Day findDayByDate(LocalDate date) {
        Optional<Day> day = repository.findByDate(date);
        if (day.isEmpty()) throw new NotFoundException("Could not find day " + date);
        return day.get();
    }

    public PageDto<Day> findAll(PageRequestDto req) {
        Page<Day> days = repository.findAll(PageRequest.of(req.getPage(), req.getSize()));
        if (days.isEmpty()) throw new NotFoundException("Could not find days");
        return pageMapper.toDto(days);
    }
}
