package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.TaskTypeModel;

public interface TaskTypeRepository extends JpaRepository<TaskTypeModel, Integer> {
}
