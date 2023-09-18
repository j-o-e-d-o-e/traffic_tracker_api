package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.WindDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WindRepository extends JpaRepository<WindDay, Long> {
    Optional<WindDay> findByDate(LocalDate date);
}
