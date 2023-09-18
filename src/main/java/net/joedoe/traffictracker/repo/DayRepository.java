package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DayRepository extends JpaRepository<Day, Long> {
    Optional<Day> findByDate(LocalDate date);

    Optional<List<Day>> findAllByDateGreaterThanEqualAndDateLessThan(LocalDate start, LocalDate end);

    @Query(value = "SELECT d FROM Day d LEFT JOIN FETCH d.flights WHERE d.date = :date")
    Optional<Day> findByDateJoinFetchFlights(@Param("date") LocalDate date);

    @Query(value = "SELECT distinct d FROM Day d LEFT JOIN FETCH d.flights")
    Optional<List<Day>> findAllJoinFetchFlights();

    Optional<Day> findDistinctFirstByOrderByDateDesc();

    @Query(value = "SELECT d.date FROM Day d ORDER BY d.date")
    List<LocalDate> findAllDates();
}
