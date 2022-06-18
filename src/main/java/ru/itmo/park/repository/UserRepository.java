package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.RoleModel;
import ru.itmo.park.model.entity.UserModel;

import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
    UserModel findByEmail(String email);
    List<UserModel> findAllByRoleAndIsBusy(RoleModel roleModel, Boolean isBusy);
}
