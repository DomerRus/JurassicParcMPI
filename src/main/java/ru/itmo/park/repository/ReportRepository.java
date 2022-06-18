package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.ReportModel;

public interface ReportRepository  extends JpaRepository<ReportModel, Integer> {
}
