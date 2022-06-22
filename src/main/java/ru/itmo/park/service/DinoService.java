package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.park.model.dto.NotificationDTO;
import ru.itmo.park.model.dto.ReportDTO;
import ru.itmo.park.model.entity.DinoModel;
import ru.itmo.park.model.entity.ReportModel;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.repository.DinoRepository;
import ru.itmo.park.repository.DinoTypeRepository;
import ru.itmo.park.repository.ReportRepository;
import ru.itmo.park.repository.UserRepository;
import ru.itmo.park.security.jwt.JwtProvider;
import ru.itmo.park.web.DinoResource;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DinoService {

    private final UserService userService;

    private final DinoRepository dinoRepository;

    private final DinoTypeRepository dinoTypeRepository;

    private final UserRepository userRepository;

    private final ReportRepository reportRepository;

    private final NotificationService notificationService;

    private final JwtProvider jwtProvider;

    public Optional<List<DinoModel>> getAllDino(){
        return Optional.of(dinoRepository.findAll());
    }

    public Optional<ReportModel> sendReport(String token, ReportDTO reportDTO){
        UserModel user = userService.findById(jwtProvider.getCurrentUser(token)).orElse(new UserModel());
        DinoModel dino = dinoRepository.getReferenceById(reportDTO.getDinoId());
        dino.setIsHealthy(reportDTO.getIsHealthy());
        dino.setAge(reportDTO.getAge());
        dino.setHeight(reportDTO.getHeight());
        dino.setWeight(reportDTO.getWeight());
        ReportModel report = ReportModel.builder()
                .age(reportDTO.getAge())
                .dino(dino)
                .isHealthy(reportDTO.getIsHealthy())
                .height(reportDTO.getHeight())
                .weight(reportDTO.getWeight())
                .user(user)
                .build();

        List<UserModel> users = userRepository.findAllByRole_Name("Manager");
        users.forEach(o->{
            NotificationDTO notif = NotificationDTO.builder()
                    .to(o.getId())
                    .header("Новый отчет!")
                    .body(String.format("Сформирован новый отчет по динозавру с id %s", report.getDino().getId()))
                    .build();
            notificationService.newNotification(notif);
        });
        return Optional.of(reportRepository.save(report));
    }

    public Optional<DinoModel> setHealthy(Boolean healthy, Integer dinoId){
        DinoModel model = dinoRepository.getReferenceById(dinoId);
        model.setIsHealthy(healthy);
        List<UserModel> users = userRepository.findAllByRole_Name("Manager");
        users.forEach(o->{
            NotificationDTO notif = NotificationDTO.builder()
                    .to(o.getId())
                    .header("Заболевание!")
                    .body(String.format("Заболел динозавр с id %s", model.getId()))
                    .build();
            notificationService.newNotification(notif);
        });
        List<UserModel> users2 = userRepository.findAllByRole_Name("Navigator");
        users2.forEach(o->{
            NotificationDTO notif = NotificationDTO.builder()
                    .to(o.getId())
                    .header("Новая задача!")
                    .body(String.format("Нужна транспортировка динозавра с id %s", model.getId()))
                    .build();
            notificationService.newNotification(notif);
        });
        return Optional.of(dinoRepository.save(model));
    }
}
