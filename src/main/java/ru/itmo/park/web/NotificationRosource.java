package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.model.dto.FirebaseDTO;
import ru.itmo.park.model.dto.NotificationDTO;
import ru.itmo.park.model.entity.FirebaseModel;
import ru.itmo.park.model.entity.NotificationModel;
import ru.itmo.park.model.entity.SettingsModel;
import ru.itmo.park.service.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class NotificationRosource {

    private final NotificationService notificationService;

    //add notification (alert etc.)
    @PostMapping
    public ResponseEntity<NotificationModel> newNotif(@RequestHeader("Authorization") String token,
                                                      @RequestBody NotificationDTO notificationDTO){
        return notificationService.newNotification(notificationDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
    //get notifications (loader notification firebase)

    //set firebase token
    @PostMapping("/token")
    public ResponseEntity<FirebaseModel> saveFirebase(@RequestHeader("Authorization") String token,
                                                      @RequestBody FirebaseDTO firebaseDTO){
        return notificationService.saveFirebaseToken(token, firebaseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/alert")
    public ResponseEntity<SettingsModel> getAlert(){
        return notificationService.getAlert()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
