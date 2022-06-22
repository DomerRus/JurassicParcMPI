package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.park.model.entity.TaskModel;

import java.util.List;
import java.util.Optional;

public interface TaskRepository  extends JpaRepository<TaskModel, Integer> {
    Optional<List<TaskModel>> findAllByTo_Id(Integer id);

    @Query(value = "SELECT * FROM tasks WHERE (tasks.from_id = :from OR tasks.to_id = :to) AND tasks.status_id < :statusId", nativeQuery = true)
    Optional<TaskModel> findFirstByFrom_IdOrTo_IdAndStatus_IdIsLessThan(Integer from, Integer to, Integer statusId);
}
