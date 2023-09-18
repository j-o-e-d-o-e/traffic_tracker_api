package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Airline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
    Optional<Airline> findByIcao(String icao);

    // GraphQL

    @Query(value = "SELECT a FROM Airline a WHERE EXISTS(SELECT f FROM Flight f WHERE f.airline.id = a.id) ORDER BY a.icao ASC")
    Optional<Page<Airline>> findAllWithPagination(Pageable pageable);
}
