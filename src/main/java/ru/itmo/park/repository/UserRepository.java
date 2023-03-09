package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.park.model.entity.RoleModel;
import ru.itmo.park.model.entity.UserModel;

import java.time.Instant;
import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

    List<UserModel> findAllByIdIsNotNullAndIsActiveOrderById(Boolean isActive);
    UserModel getById(Integer id);
    UserModel findByEmailAndIsActive(String email, Boolean isActive);
    List<UserModel> findAllByRoleAndIsBusyAndIsActive(RoleModel roleModel, Boolean isBusy, Boolean isActive);
    List<UserModel> findAllByRole_Name(String roleModel);
    @Query(value = "SELECT users.* FROM users WHERE users.id NOT IN (SELECT user_id FROM schedules WHERE date_time >= :dateTime) AND users.is_active = true", nativeQuery = true)
    List<UserModel> findUserFreeSchedule(Instant dateTime);
}
