package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<List<Flight>> getFlightsByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime dateBefore, LocalDateTime dateAfter);

    Optional<Page<Flight>> getFlightsByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime dateBefore, LocalDateTime dateAfter, Pageable pageable);

    @Query(value = "SELECT f FROM Flight f WHERE f.plane.icao = :icao order by f.dateTime desc")
    Optional<Page<Flight>> findByPlaneIcao(@Param("icao") String icao, Pageable pageable);

    // GraphQL

    @Query(value = "SELECT f FROM Flight f WHERE f.airline.icao = :icao order by f.dateTime desc")
    Optional<Page<Flight>> findByAirlineIcao(@Param("icao") String icao, Pageable pageable);

    @Query(value = "SELECT f FROM Flight f WHERE f.departure.icao = :icao order by f.dateTime desc")
    Optional<Page<Flight>> findByAirportIcao(@Param("icao") String icao, Pageable pageable);
}
