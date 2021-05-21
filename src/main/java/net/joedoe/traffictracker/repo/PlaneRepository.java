package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Plane;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface PlaneRepository extends JpaRepository<Plane, Long> {

    Optional<Plane> findByIcao(String icao);

    // GraphQL

    @Query(value = "SELECT p FROM Plane p WHERE EXISTS(SELECT f FROM Flight f WHERE f.plane.id = p.id) ORDER BY p.icao ASC")
    Optional<Page<Plane>> findAllWithPagination(Pageable pageable);
}
