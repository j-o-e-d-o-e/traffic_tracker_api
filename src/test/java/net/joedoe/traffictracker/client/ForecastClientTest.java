package net.joedoe.traffictracker.client;

import net.joedoe.traffictracker.repo.ForecastRepository;
import net.joedoe.traffictracker.repo.ForecastScoreRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Ignore("To avoid remote call to my heroku-django-api")
public class ForecastClientTest {
    @Mock
    private ForecastRepository repository;
    @Mock
    private ForecastScoreRepository scoreRepository;
    private ForecastClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        client = new ForecastClient(repository, scoreRepository);
    }

    @Test
    public void predict() {
        client.predict();
    }

    @Test
    public void score() {
        client.score();
    }
}