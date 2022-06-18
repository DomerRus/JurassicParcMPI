package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.RoleModel;
public interface RoleRepository extends JpaRepository<RoleModel, Integer> {
    RoleModel findByName(String name);
}
