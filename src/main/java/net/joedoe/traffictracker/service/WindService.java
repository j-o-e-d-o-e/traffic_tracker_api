package net.joedoe.traffictracker.service;

import net.joedoe.traffictracker.client.WindClient;
import net.joedoe.traffictracker.dto.WindDto;
import net.joedoe.traffictracker.model.WindDay;
import net.joedoe.traffictracker.repo.WindRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WindService {

    private final WindClient client;
    private final WindRepository repository;

    public WindService(WindClient client, WindRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    public void fetchWind(){
        WindDto windDto = client.fetchWind();
        addWind(windDto);
    }


    public void addWind(WindDto windDto) {
        WindDay day = repository.findByDate(windDto.getDateTime().toLocalDate()).orElse(null);
        if (day == null) day = new WindDay(LocalDate.now());
        day.addWind(windDto);
        repository.save(day);
    }

    public WindDay getDayByDate(LocalDate date) {
        return repository.findByDate(date).orElse(null);
    }

    void deleteAll(){
        repository.deleteAll();
    }
}
