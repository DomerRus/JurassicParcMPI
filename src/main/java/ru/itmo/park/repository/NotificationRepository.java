package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.NotificationModel;

public interface NotificationRepository  extends JpaRepository<NotificationModel, Integer> {
}
