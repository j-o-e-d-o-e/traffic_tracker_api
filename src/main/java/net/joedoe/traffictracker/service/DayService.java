package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.exception.ResourceNotFoundException;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.model.Wind;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@PropertySource("classpath:locale.properties")
@Slf4j
@Service
public class DayService {
    private DayRepository repository;

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
        return repository.getDayById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Day getDay(LocalDate date) {
        return repository.getDayByDate(date)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Day> getWeek(LocalDate date) {
        return repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusWeeks(1))
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Day> getMonth(LocalDate date) {
        return repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusMonths(1))
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Day> getYear(LocalDate date) {
        return repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusYears(1))
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Day> findAll() {
        return repository.findAll();
    }
}
