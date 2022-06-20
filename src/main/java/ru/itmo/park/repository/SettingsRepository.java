package ru.itmo.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.park.model.entity.SettingsModel;

public interface SettingsRepository extends JpaRepository<SettingsModel, Integer> {
    SettingsModel findFirstByName(String name);
}
