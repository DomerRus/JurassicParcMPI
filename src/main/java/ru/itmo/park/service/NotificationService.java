package ru.itmo.park.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.itmo.park.model.dto.FirebaseDTO;
import ru.itmo.park.model.dto.NotificationDTO;
import ru.itmo.park.model.entity.FirebaseModel;
import ru.itmo.park.model.entity.NotificationModel;
import ru.itmo.park.model.entity.SettingsModel;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.repository.FirebaseRepository;
import ru.itmo.park.repository.NotificationRepository;
import ru.itmo.park.repository.SettingsRepository;
import ru.itmo.park.security.jwt.JwtProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final UserService userService;

    private final JwtProvider jwtProvider;

    private final FirebaseRepository firebaseRepository;

    private final NotificationRepository notificationRepository;

    private final SettingsRepository settingsRepository;

    public Optional<FirebaseModel> saveFirebaseToken(String token, FirebaseDTO firebaseDTO){
        Integer userId = jwtProvider.getCurrentUser(token);
        UserModel userModel = userService.findById(userId).orElse(new UserModel());
        FirebaseModel firebaseModel = FirebaseModel.builder()
                .token(firebaseDTO.getToken())
                .user(userModel)
                .build();
        return Optional.of(firebaseRepository.save(firebaseModel));
    }

    public Optional<NotificationModel> newNotification(NotificationDTO notificationDTO){
        UserModel userModel = userService.findById(notificationDTO.getTo()).orElse(new UserModel());
        NotificationModel notif = NotificationModel.builder()
                .header(notificationDTO.getHeader())
                .body(notificationDTO.getBody())
                .user(userModel)
                .isAlert(saveAlert(notificationDTO.getIsAlert()))
                .isSend(Boolean.FALSE)
                .build();
        return Optional.of(notificationRepository.save(notif));
    }

    public Optional<SettingsModel> getAlert(){
        return Optional.ofNullable(settingsRepository.findFirstByName("alert"));
    }

    @Scheduled(fixedRate = 5000)
    private void autoNotification() {
        List<NotificationModel> notifList = notificationRepository.findAllByIsSendIsFalse();
        List<Message> messages = new ArrayList<>();
        notifList.forEach(item -> {
            List<FirebaseModel> tokenList = new ArrayList<>();
            if(item.getIsAlert()){
                tokenList = firebaseRepository.findAll();
            } else {
                tokenList = firebaseRepository.findAllByUser_Id(item.getUser().getId());
            }
            final Notification notification = Notification.builder()
                    .setBody(item.getBody())
                    .setTitle(item.getHeader())
                    .setImage("https://www.pikpng.com/pngl/b/144-1448855_alert-png-alert-red-icon-png-clipart.png")
                    .build();
            tokenList.forEach(token -> {
                messages.add(Message.builder()
                        .setToken(token.getToken())
                        .setNotification(notification)
                        .build());
            });
            item.setIsSend(Boolean.TRUE);
            notificationRepository.save(item);
        });
        log.info("Send notification...");
        try{
            if (messages.size()==0) return;
            FirebaseMessaging.getInstance()
                    .sendAllAsync(messages);
        }catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }


    private Boolean saveAlert(Boolean alert){
        SettingsModel setting = settingsRepository.findFirstByName("alert");
        if(setting!=null){
            setting.setValue(alert.toString());
            settingsRepository.save(setting);
            return Boolean.parseBoolean(setting.getValue());
        } else {
            SettingsModel newSetting = SettingsModel.builder()
                    .name("alert")
                    .value(alert.toString())
                    .build();
            settingsRepository.save(newSetting);
            return Boolean.parseBoolean(newSetting.getValue());
        }
    }
}
