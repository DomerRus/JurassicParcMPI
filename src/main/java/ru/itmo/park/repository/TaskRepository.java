package ru.itmo.park.repository;

import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.park.model.entity.TaskModel;

import java.util.List;
import java.util.Optional;

public interface TaskRepository  extends JpaRepository<TaskModel, Integer> {
    Optional<List<TaskModel>> findAllByTo_Id(Integer id);

    @Query(value = "SELECT * FROM tasks WHERE tasks.to_id = :to AND tasks.status_id < :statusId AND tasks.is_active = True ORDER BY group_id DESC LIMIT 1", nativeQuery = true)
    Optional<TaskModel> findFirstByTo_IdAndStatus_IdIsLessThan(Integer to, Integer statusId);

    @Query(value = "SELECT * FROM tasks WHERE tasks.group_id = :groupId AND tasks.status_id < 5 AND tasks.is_active = True", nativeQuery = true)
    Optional<List<TaskModel>> findAllByFromId(Long groupId);

    @Query(value = "SELECT * FROM tasks WHERE tasks.from_id = :to AND tasks.group_id = (SELECT group_id FROM tasks WHERE tasks.from_id = :to AND status_id < 5 AND tasks.is_active = True ORDER BY group_id DESC LIMIT 1) AND status_id < 5 AND tasks.is_active = True", nativeQuery = true)
    Optional<List<TaskModel>> findAllByFromId(Integer to);

    @Query(value = "SELECT * FROM tasks WHERE tasks.from_id = :to AND tasks.group_id = (SELECT group_id FROM tasks WHERE tasks.from_id = :to AND tasks.is_active = True ORDER BY group_id DESC LIMIT 1) AND status_id = 4 AND tasks.is_active = True", nativeQuery = true)
    List<TaskModel> findAllByFromIdAndCancel(Integer to);
    @Query(value = "SELECT group_id FROM tasks ORDER BY group_id desc LIMIT 1 ", nativeQuery = true)
    Optional<Long> getTopByGroupId();

    @Query(value = "UPDATE tasks SET is_active = False WHERE id in (SELECT id FROM tasks WHERE tasks.group_id = :groupId AND tasks.is_active = True)", nativeQuery = true)
    void disableTasksByGroup(Long groupId);

    @Query(value = "SELECT * FROM tasks WHERE tasks.group_id = :groupId AND tasks.is_active = True", nativeQuery = true)
    List<TaskModel> tasksForDesyModel(Long groupId);
}
