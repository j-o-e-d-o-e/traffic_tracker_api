package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface PlaneRepository extends JpaRepository<Plane, Long> {

    Plane getPlaneById(Long id);

    Optional<Page<Plane>> getPlanesByDateBetween(LocalDateTime dateBefore, LocalDateTime dateAfter, Pageable pageable);

    Optional<Page<Plane>> getPlanesByIcao(String icao, Pageable pageable);

    @Query("SELECT p from Plane p WHERE p.altitude = (SELECT MAX(altitude) FROM Plane)")
    Optional<List<Plane>> getPlanesWithMaxAltitude();

    @Query("SELECT p from Plane p WHERE p.speed = (SELECT MAX(speed) FROM Plane)")
    Optional<List<Plane>> getPlanesWithMaxSpeed();

    @Query("SELECT p from Plane p WHERE p.altitude = (SELECT MIN(altitude) FROM Plane)")
    Optional<List<Plane>> getPlanesWithMinAltitude();

    @Query("SELECT p from Plane p WHERE p.speed = (SELECT MIN(speed) FROM Plane)")
    Optional<List<Plane>> getPlanesWithMinSpeed();
}
