package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.MonthMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MonthService {
    private final DayRepository repository;

    public MonthService(DayRepository repository) {
        this.repository = repository;
    }

    public MonthDto getMonthLatest() {
        Optional<Day> day = repository.findDistinctFirstByOrderByDateDesc();
        if (day.isEmpty()) throw new NotFoundException("Could not find any month");
        return getMonthByDate(day.get().getDate().withDayOfMonth(1));
    }

    public MonthDto getMonthByDate(LocalDate date) {
        List<Day> month = repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusMonths(1)).orElse(null);
        if (month == null || month.isEmpty())
            throw new NotFoundException("Could not find month " + date.getMonthValue() + "/" + date.getYear());
        return MonthMapper.toDto(date, month, hasNeighbour(date.minusMonths(1)), hasNeighbour(date.plusMonths(1)));
    }

    private boolean hasNeighbour(LocalDate date) {
        Optional<List<Day>> month = repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusMonths(1));
        return month.isPresent() && !month.get().isEmpty();
    }
}
