package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.model.ForecastScore;
import net.joedoe.traffictracker.repo.ForecastScoreRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastScoreService {
    private final ForecastScoreRepository repository;

    public ForecastScoreService(ForecastScoreRepository repository) {
        this.repository = repository;
    }

    public ForecastScore find() {
        return repository.findAll().get(0);
    }
}
