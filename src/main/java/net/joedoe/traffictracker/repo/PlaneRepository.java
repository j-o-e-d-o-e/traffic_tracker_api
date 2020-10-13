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

    Optional<Plane> getPlaneById(Long id);

    Optional<List<Plane>> getPlanesByDateBetweenOrderByDateDesc(LocalDateTime dateBefore, LocalDateTime dateAfter);

    Optional<Page<Plane>> getPlanesByDateBetweenOrderByDateDesc(LocalDateTime dateBefore, LocalDateTime dateAfter, Pageable pageable);

    Optional<Page<Plane>> getPlanesByIcaoOrderByDateDesc(String icao, Pageable pageable);
}
