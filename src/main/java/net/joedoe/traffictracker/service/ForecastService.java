package net.joedoe.traffictracker.service;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.ml.model.ForecastDaily;
import net.joedoe.traffictracker.repo.ForecastRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ForecastService {
    private ForecastRepository repository;

    public ForecastService(ForecastRepository repository) {
        this.repository = repository;
    }

    public List<ForecastDaily> findAll() {
        return repository.findAll();
    }
}
