package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.DinoModel;

import java.util.List;
import java.util.Optional;

public interface DinoRepository extends JpaRepository<DinoModel, Integer> {
    Optional<List<DinoModel>> findAllByIsActiveOrderById(Boolean isActive);
}
