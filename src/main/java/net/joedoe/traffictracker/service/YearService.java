package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.YearMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class YearService {
    private final DayRepository repository;

    public YearService(DayRepository repository) {
        this.repository = repository;
    }

    public YearDto getYear(LocalDate date) {
        Optional<List<Day>> year = repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusYears(1));
        if (!year.isPresent() || year.get().isEmpty()) {
            throw new NotFoundException("Could not find year " + date.getYear());
        }
        return YearMapper.toDto(date, year.get());
    }
}
