package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.exception.NotFoundException;
import net.joedoe.traffictracker.mapper.WeekMapper;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.repo.DayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class WeekService {
    private final DayRepository repository;

    public WeekService(DayRepository repository) {
        this.repository = repository;
    }

    public WeekDto getWeek(LocalDate date) {
        Optional<List<Day>> week = repository.findAllByDateGreaterThanEqualAndDateLessThan(date, date.plusWeeks(1));
        if (!week.isPresent() || week.get().isEmpty()) {
            throw new NotFoundException("Could not find week " + date + " to " + date.plusWeeks(1));
        }
        return WeekMapper.toDto(date, week.get());
    }
}
