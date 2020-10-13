package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.ForecastScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface ForecastScoreRepository extends JpaRepository<ForecastScore, Long> {
}
