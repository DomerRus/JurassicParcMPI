package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.RoleModel;
import ru.itmo.park.model.entity.UserModel;

import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

    List<UserModel> findAllByIdIsNotNullOrderById();
    UserModel getById(Integer id);
    UserModel findByEmail(String email);
    List<UserModel> findAllByRoleAndIsBusy(RoleModel roleModel, Boolean isBusy);
    List<UserModel> findAllByRole_Name(String roleModel);
}
