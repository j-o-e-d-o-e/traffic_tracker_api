package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface DayRepository extends JpaRepository<Day, Long> {

    Optional<Day> getDayById(Long id);

    Optional<Day> getDayByDate(LocalDate date);

    Optional<List<Day>> findAllByDateGreaterThanEqualAndDateLessThan(LocalDate start, LocalDate end);
}
