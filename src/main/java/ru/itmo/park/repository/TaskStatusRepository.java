package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.TaskStatusModel;
import ru.itmo.park.model.entity.TaskTypeModel;

public interface TaskStatusRepository extends JpaRepository<TaskStatusModel, Integer> {
}
