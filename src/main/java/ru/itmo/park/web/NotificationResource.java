package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.FirebaseDTO;
import ru.itmo.park.model.dto.NotificationDTO;
import ru.itmo.park.model.entity.FirebaseModel;
import ru.itmo.park.model.entity.NotificationModel;
import ru.itmo.park.model.entity.SettingsModel;
import ru.itmo.park.service.NotificationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class NotificationResource {

    private final NotificationService notificationService;

    //add notification (alert etc.)
    @PostMapping
    public ResponseEntity<NotificationModel> newNotif(@RequestHeader("Authorization") String token,
                                                      @RequestBody NotificationDTO notificationDTO) throws UserNotFoundException {
        return notificationService.newNotification(notificationDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
    //get notifications (loader notification firebase)
    @GetMapping
    public ResponseEntity<List<NotificationModel>> getAllNotif(@RequestHeader("Authorization") String token){
        return notificationService.getAllNotif(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    //set firebase token
    @PostMapping("/token")
    public ResponseEntity<FirebaseModel> saveFirebase(@RequestHeader("Authorization") String token,
                                                      @RequestBody FirebaseDTO firebaseDTO) throws UserNotFoundException {
        return notificationService.saveFirebaseToken(token, firebaseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleJsonMappingException(Exception ex) {
        JSONObject errorResponse = new JSONObject();
        String[] name = ex.getClass().getName().split("\\.");
        errorResponse.put("error", name[name.length-1]);
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(errorResponse.toString());
    }
}
