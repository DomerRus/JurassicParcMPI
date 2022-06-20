package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.park.model.entity.SettingsModel;
import ru.itmo.park.service.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/alert")
public class AlertResource {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<SettingsModel> getAlert(){
        return notificationService.getAlert()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
