package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.TaskModel;

import java.util.List;
import java.util.Optional;

public interface TaskRepository  extends JpaRepository<TaskModel, Integer> {
    Optional<List<TaskModel>> findAllByTo_Id(Integer id);

    Optional<TaskModel> findFirstByFrom_IdAndStatus_IdIsNot(Integer id, Integer statusId);
}
