package net.joedoe.traffictracker.repo;

import net.joedoe.traffictracker.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findByName(String name);
}
