package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.YearMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class YearService {
    private final DayRepository repository;

    public YearService(DayRepository repository) {
        this.repository = repository;
    }

    public YearDto getYearLatest() {
        Optional<Day> day = repository.findDistinctFirstByOrderByDateDesc();
        if (day.isEmpty()) throw new NotFoundException("Could not find any month");
        return getYearByDate(day.get().getDate().withMonth(1).withDayOfMonth(1));
    }

    public YearDto getYearByDate(LocalDate date) {
        Optional<List<Day>> year = repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusYears(1));
        if (year.isEmpty() || year.get().isEmpty())
            throw new NotFoundException("Could not find year " + date.getYear());
        return YearMapper.toDto(date, year.get(), hasNeighbour(date.minusYears(1)), hasNeighbour(date.plusYears(1)));
    }

    private boolean hasNeighbour(LocalDate date) {
        Optional<List<Day>> year = repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusYears(1));
        return year.isPresent() && !year.get().isEmpty();
    }
}
