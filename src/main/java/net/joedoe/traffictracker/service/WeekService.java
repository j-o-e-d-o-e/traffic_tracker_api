package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.WeekMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WeekService {
    private final DayRepository repository;

    public WeekService(DayRepository repository) {
        this.repository = repository;
    }

    public WeekDto getWeek(LocalDate date) {
        List<Day> week = repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusWeeks(1)).orElse(null);
        if (week == null || week.isEmpty()) {
            throw new NotFoundException("Could not find week " + date + " to " + date.plusWeeks(1));
        }
        return WeekMapper.toDto(date, week, hasNeighbour(date.minusWeeks(1)), hasNeighbour(date.plusWeeks(1)));
    }

    private boolean hasNeighbour(LocalDate date) {
        Optional<List<Day>> week = repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusWeeks(1));
        return week.isPresent() && !week.get().isEmpty();
    }
}
