package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.ForecastDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastRepository extends JpaRepository<ForecastDay, Long> {
}
