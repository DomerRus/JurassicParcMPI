package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.model.dto.AlertDTO;
import ru.itmo.park.model.entity.SettingsModel;
import ru.itmo.park.service.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/alarm")
@CrossOrigin(origins = "*")
public class AlertResource {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<SettingsModel> getAlert(){
        return notificationService.getAlert()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<SettingsModel> setAlert(@RequestBody AlertDTO alertDTO){
        return notificationService.setAlert(alertDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
