package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.DinoModel;

public interface DinoRepository extends JpaRepository<DinoModel, Integer> {
}
