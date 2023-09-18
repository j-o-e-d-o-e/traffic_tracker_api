package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.bootstrap.DaysInitTest;
import net.joedoe.traffictracker.bootstrap.ForecastsInitTest;
import net.joedoe.traffictracker.client.ForecastClient;
import net.joedoe.traffictracker.model.ForecastDay;
import net.joedoe.traffictracker.repo.DayRepository;
import net.joedoe.traffictracker.repo.ForecastRepository;
import net.joedoe.traffictracker.repo.ForecastScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {
    @InjectMocks
    private ForecastService service;
    @Mock
    ForecastClient client;
    @SuppressWarnings("unused")
    @Mock
    ForecastRepository repository;
    @SuppressWarnings("unused")
    @Mock
    ForecastScoreRepository scoreRepository;
    @Mock
    DayRepository dayRepository;
    List<ForecastDay> days = ForecastsInitTest.createForecastDays();

    @Test
    void predict() {
        when(client.fetchForecasts()).thenReturn(days);
        service.predict();
    }

    @Test
    void score(){
        when(dayRepository.findAllByDateGreaterThanEqualAndDateLessThan(any(), any())).thenReturn(Optional.of(DaysInitTest.createDays(30)));
        when(client.fetchScores(any(), any())).thenReturn(ForecastsInitTest.createScore());
        service.score();
    }
}