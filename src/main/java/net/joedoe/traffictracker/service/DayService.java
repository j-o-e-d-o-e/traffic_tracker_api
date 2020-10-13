package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.exception.ResourceNotFoundException;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.model.Wind;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@PropertySource({"classpath:departureAirport.properties", "classpath:locale.properties"})
@Slf4j
@Service
public class DayService {
    private final DayRepository repository;
    @Value("${prefix}")
    public String prefix;
    @Value("${prefix1}")
    public String prefix1;
    @Value("${prefix2}")
    public String prefix2;

    public DayService(DayRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "${timezone}")
    public void addNewDay() {
        LocalDate now = LocalDate.now();
        repository.save(new Day(now));
        Day dayBefore = repository.getDayByDate(now.minusDays(30)).orElse(null);
        if (dayBefore != null) {
            dayBefore.clearPlanes();
            repository.save(dayBefore);
        }
    }

    public void addPlane(Plane plane) {
        Day currentDay = repository.getDayByDate(plane.getDate().toLocalDate()).orElse(null);
        if (currentDay != null) {
            currentDay.addPlane(plane);
            repository.save(currentDay);
        }
    }

    public void addWind(Wind wind) {
        Day currentDay = repository.getDayByDate(wind.getDate().toLocalDate()).orElse(null);
        if (currentDay != null) {
            currentDay.addWind(wind);
            repository.save(currentDay);
        }
    }

    public Day getDayById(Long id) {
        return repository.getDayById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public Day getDay(LocalDate date) {
        return repository.getDayByDate(date).orElseThrow(ResourceNotFoundException::new);
    }

    public List<Day> getWeek(LocalDate date) {
        return repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusWeeks(1)).orElseThrow(ResourceNotFoundException::new);
    }

    public List<Day> getMonth(LocalDate date) {
        return repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusMonths(1)).orElseThrow(ResourceNotFoundException::new);
    }

    public List<Day> getYear(LocalDate date) {
        return repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusYears(1)).orElseThrow(ResourceNotFoundException::new);
    }

    public List<Day> findAll() {
        return repository.findAll();
    }

    public void setDepartures(LocalDate date) {
        Day day = repository.getDayByDate(date).orElse(null);
        if (day == null)
            return;
        int departuresContinental = 0, departuresInternational = 0, departuresNational = 0, departuresUnknown = 0;
        Map<String, Integer> airportsOccurrences = new HashMap<>();
        for (Plane plane : day.getPlanes()) {
            if (plane.getDepartureAirport() == null) {
                departuresUnknown++;
            } else {
                if (plane.getDepartureAirport().startsWith(prefix)) {
                    departuresNational++;
                } else {
                    if (plane.getDepartureAirport().startsWith(prefix1) || plane.getDepartureAirport().startsWith(prefix2)) {
                        departuresInternational++;
                    } else {
                        departuresContinental++;
                    }
                }
                String departure = plane.getDepartureAirportName() != null ? plane.getDepartureAirportName() : plane.getDepartureAirport();
                if (airportsOccurrences.containsKey(departure)) {
                    airportsOccurrences.put(departure, airportsOccurrences.get(departure) + 1);
                } else {
                    airportsOccurrences.put(departure, 1);
                }
            }
        }
        if (day.getTotal() == 0 || (departuresContinental == 0 && departuresInternational == 0 && departuresNational == 0 && departuresUnknown == 0))
            return;
        float avgContinental = Math.round(departuresContinental / (float) day.getTotal() * 100) / 100f;
        day.setDeparturesContinental(avgContinental);
        float avgInternational = Math.round(departuresInternational / (float) day.getTotal() * 100) / 100f;
        day.setDeparturesInternational(avgInternational);
        float avgNational = Math.round(departuresNational / (float) day.getTotal() * 100) / 100f;
        day.setDeparturesNational(avgNational);
        float avgUnknown = 1 - avgContinental - avgInternational - avgNational;
        day.setDeparturesUnknown(avgUnknown);

        day.setDeparturesContinentalAbs(departuresContinental);
        day.setDeparturesInternationalAbs(departuresInternational);
        day.setDeparturesNationalAbs(departuresNational);
        day.setDeparturesUnknownAbs(departuresUnknown);

        Map<String, Integer> topFive = airportsOccurrences.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        day.setDeparturesTop(topFive);
        repository.save(day);
    }
}
