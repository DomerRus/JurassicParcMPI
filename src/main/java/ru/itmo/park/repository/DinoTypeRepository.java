package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.DinoTypeModel;

public interface DinoTypeRepository extends JpaRepository<DinoTypeModel, Integer> {
}
