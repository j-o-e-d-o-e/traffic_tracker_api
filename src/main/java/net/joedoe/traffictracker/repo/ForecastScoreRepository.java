package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.ForecastScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastScoreRepository extends JpaRepository<ForecastScore, Long> {
}
