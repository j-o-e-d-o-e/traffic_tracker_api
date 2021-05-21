package net.joedoe.traffictracker.service;

import graphql.GraphQLException;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.dto.PageDto;
import net.joedoe.traffictracker.dto.PageRequestDto;
import net.joedoe.traffictracker.dto.WindDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.DayMapper;
import net.joedoe.traffictracker.mapper.PageMapper;
import net.joedoe.traffictracker.model.Airline;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Flight;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.repo.DayRepository;
import net.joedoe.traffictracker.util.PropertiesHandler;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@PropertySource({"classpath:locale.properties"})
@Slf4j
@Service
public class DayService {
    private final DayRepository repository;
    private final PlaneService planeService;
    private final AirlineService airlineService;
    private int flightsSavedInDays;
    private final PageMapper<Day> pageMapper = new PageMapper<>();

    public DayService(DayRepository repository, PlaneService planeService, AirlineService airlineService) {
        this.repository = repository;
        this.planeService = planeService;
        this.airlineService = airlineService;
        try {
            Properties prop = PropertiesHandler.getProperties("src/main/resources/flights-db.properties");
            this.flightsSavedInDays = Integer.parseInt(prop.getProperty("flights.saved.in.days"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "${timezone}")
    public void addDay() {
        LocalDate now = LocalDate.now();
        Day day = repository.save(new Day(now));
        // clear flights before date minus days = 'flightsSavedInDays' and planes without flights
        Day dayBefore = repository.findByDateJoinFetchFlights(now.minusDays(flightsSavedInDays)).orElse(null);
        if (dayBefore != null) {
            dayBefore.clearFlights();
            repository.save(dayBefore);
        }
        planeService.delete();
        log.info("New day: " + day);
    }

    public void addFlight(String icao, Flight flight) {
        Plane plane = planeService.findOrCreate(icao);
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

    public void addWind(WindDto windDto) {
        Day currentDay = repository.findByDate(windDto.getDateTime().toLocalDate()).orElse(null);
        if (currentDay != null) {
            currentDay.addWind(windDto);
            repository.save(currentDay);
            log.info("New wind: " + windDto);
        }
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

    public DayDto getDayByDate(LocalDate date) {
        Optional<Day> day = repository.findByDate(date);
        if (!day.isPresent()) {
            throw new NotFoundException("Could not find day " + date);
        }
        return DayMapper.toDto(day.get());
    }

    // GraphQL

    public Day findDayByDate(LocalDate date) {
        Optional<Day> day = repository.findByDate(date);
        if (!day.isPresent()) {
            throw new GraphQLException("Could not find day " + date);
        }
        return day.get();
    }

    public PageDto<Day> findAll(PageRequestDto req) {
        Page<Day> days = repository.findAll(PageRequest.of(req.getPage(), req.getSize()));
        if (days.isEmpty()) {
            throw new GraphQLException("Could not find days");
        }
        return pageMapper.toDto(days);

    }
}
