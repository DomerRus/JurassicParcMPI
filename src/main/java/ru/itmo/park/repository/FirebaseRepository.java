package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.FirebaseModel;
import ru.itmo.park.model.entity.UserModel;

import java.util.List;

public interface FirebaseRepository extends JpaRepository<FirebaseModel, Integer> {
    List<FirebaseModel> findAllByUser_Id(Integer userId);

    FirebaseModel findFirstByToken(String token);
}
