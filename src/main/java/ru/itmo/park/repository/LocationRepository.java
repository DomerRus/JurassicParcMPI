package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.LocationModel;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationModel, Integer> {
    Optional<LocationModel> findById(Integer id);
}
