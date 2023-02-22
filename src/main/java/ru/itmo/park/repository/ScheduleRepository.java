package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.DinoModel;
import ru.itmo.park.model.entity.ScheduleModel;
import ru.itmo.park.model.entity.UserModel;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<ScheduleModel, Integer> {
    Optional<List<ScheduleModel>> findAllByUser_IdAndDateTimeGreaterThanEqual(Integer userId, Instant date);

    Optional<List<ScheduleModel>> findAllByDateTimeGreaterThan(Instant date);
}
