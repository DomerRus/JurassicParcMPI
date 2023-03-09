package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.NotificationModel;

import java.util.List;

public interface NotificationRepository  extends JpaRepository<NotificationModel, Integer> {
    List<NotificationModel> findAllByIsSendIsFalse();

    List<NotificationModel> findAllByUser_IdOrderByIdDesc(Integer id);
}
