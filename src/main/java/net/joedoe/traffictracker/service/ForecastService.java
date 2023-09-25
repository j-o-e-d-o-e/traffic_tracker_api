package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.client.ForecastClient;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.model.ForecastScore;
import net.joedoe.traffictracker.repo.DayRepository;
import net.joedoe.traffictracker.repo.ForecastRepository;
import net.joedoe.traffictracker.repo.ForecastScoreRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForecastService {
    private final DayRepository dayRepository;
    private final ForecastRepository repository;
    private final ForecastScoreRepository scoreRepository;
    private final ForecastClient client;

    public ForecastService(DayRepository dayRepository, ForecastRepository repository, ForecastScoreRepository scoreRepository, ForecastClient client) {
        this.dayRepository = dayRepository;
        this.repository = repository;
        this.scoreRepository = scoreRepository;
        this.client = client;
    }

    public List<ForecastDay> getForecasts() {
        return repository.findAll();
    }

    public ForecastScore getScore() {
        return scoreRepository.findAll().get(0);
    }


    // At midnight, 06:00 AM, 09:00 AM, 12:00 PM, 03:00 PM, 06:00 PM and 09:00 PM
    // 8 calls to weather-api and to django-api/day
    @Scheduled(cron = "0 0 0,6,9,12,15,18,21 * * *", zone = "${locale.timezone}")
    public void predict() {
        List<ForecastDay> days = client.fetchForecasts();
        repository.deleteAll();
        repository.saveAll(days);
    }

    // At midnight
    @Scheduled(cron = "0 0 0 * * *", zone = "${locale.timezone}")
    public void score() {
        LocalDate now = LocalDate.now();
        List<Day> days = dayRepository.findAllByDateGreaterThanEqualAndDateLessThan(now.minusMonths(1), now).orElse(null);
        if (days == null || days.isEmpty()) return;
        List<Integer> flights = new ArrayList<>();
        List<Integer> winds = new ArrayList<>();
        for (Day day : days) {
            if (day.getDate().isBefore(LocalDate.of(2023, 9, 11))) continue; // TODO: tbd after 2023-10-11
            int[] f = day.getHoursFlight();
            int[] w = day.getHoursWind();
            for (int i = 6; i < 24; i += 3) {
                flights.add(f[i]);
                winds.add(w[i]);
            }
        }
        ForecastScore score = client.fetchScores(flights, winds);
        scoreRepository.deleteAll();
        scoreRepository.save(score);
    }
}
