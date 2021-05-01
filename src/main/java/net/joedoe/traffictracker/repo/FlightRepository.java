package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> getFlightById(Long id);

    Optional<List<Flight>> getFlightsByDateBetweenOrderByDateDesc(LocalDateTime dateBefore, LocalDateTime dateAfter);

    Optional<Page<Flight>> getFlightsByDateBetweenOrderByDateDesc(LocalDateTime dateBefore, LocalDateTime dateAfter, Pageable pageable);

    Optional<Page<Flight>> getFlightsByIcaoOrderByDateDesc(String icao, Pageable pageable);
}
