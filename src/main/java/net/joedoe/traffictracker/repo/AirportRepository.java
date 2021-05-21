package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Airport;
import net.joedoe.traffictracker.model.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface AirportRepository extends JpaRepository<Airport, Long> {

    Optional<Airport> findByIcao(String icao);

    // GraphQL

    @Query(value = "SELECT a FROM Airport a WHERE EXISTS(SELECT f FROM Flight f WHERE f.departure.id = a.id) ORDER BY a.icao ASC")
    Optional<Page<Airport>> findAllWithPagination(Pageable pageable);

    @Query(value = "SELECT a FROM Airport a WHERE a.region = :region AND EXISTS(SELECT f FROM Flight f WHERE f.departure.id = a.id) ORDER BY a.icao ASC")
    Optional<Page<Airport>> findAllByRegion(@Param("region") Region region, Pageable pageable);
}
