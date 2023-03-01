package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.itmo.park.exception.DinoNotFoundException;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.DinoDTO;
import ru.itmo.park.model.dto.NotificationDTO;
import ru.itmo.park.model.dto.ReportDTO;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.dto.response.DinoResponseDTO;
import ru.itmo.park.model.entity.*;
import ru.itmo.park.repository.*;
import ru.itmo.park.security.jwt.JwtProvider;
import ru.itmo.park.web.DinoResource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DinoService {

    private final UserService userService;

    private final DinoRepository dinoRepository;

    private final DinoTypeRepository dinoTypeRepository;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final ReportRepository reportRepository;

    private final NotificationService notificationService;

    private final JwtProvider jwtProvider;

    public Optional<List<DinoModel>> getAllDino(){
        return dinoRepository.findAllByIsActiveOrderById(Boolean.TRUE);
    }

    public Optional<List<DinoResponseDTO>> getAllDinoRecommend(){
        return Optional.of(
                dinoRepository.findAllByIsActiveOrderById(Boolean.TRUE).orElse(List.of())
                        .stream()
                        .map(o -> new DinoResponseDTO(o).setRecommend(o.getTraining()>=70 ? Boolean.TRUE : Boolean.FALSE))
                        .sorted((o1, o2)->o2.getIsRecommend().
                                compareTo(o1.getIsRecommend()))
                        .collect(Collectors.toList()));
    }

    public Optional<List<DinoTypeModel>> getAllType(){return Optional.of(dinoTypeRepository.findAll());}

    public Optional<ReportModel> sendReport(String token, ReportDTO reportDTO) throws UserNotFoundException {
        UserModel user = userService.findById(jwtProvider.getCurrentUser(token)).orElse(new UserModel());
        DinoModel dino = dinoRepository.getReferenceById(reportDTO.getDinoId());
        dino.setIsHealthy(reportDTO.getIsHealthy());
        dino.setAge(reportDTO.getAge());
        dino.setHeight(reportDTO.getHeight());
        dino.setWeight(reportDTO.getWeight());
        dino.setTraining(reportDTO.getTraining());
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
            try {
                notificationService.newNotification(notif);
            } catch (UserNotFoundException e) {
                throw new RuntimeException(e);
            }
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
            try {
                notificationService.newNotification(notif);
            } catch (UserNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        List<UserModel> users2 = userRepository.findAllByRole_Name("Navigator");
        users2.forEach(o->{
            NotificationDTO notif = NotificationDTO.builder()
                    .to(o.getId())
                    .header("Новая задача!")
                    .body(String.format("Нужна транспортировка динозавра с id %s", model.getId()))
                    .build();
            try {
                notificationService.newNotification(notif);
            } catch (UserNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return Optional.of(dinoRepository.save(model));
    }

    public Optional<DinoModel> addNewDino(DinoDTO model) {
        DinoTypeModel type = dinoTypeRepository.findFirstByType(model.getType());
        LocationModel location = locationRepository.findById(1).get();
        return Optional.of(dinoRepository.save(new DinoModel(model, type, location)));
    }

    public Optional<DinoModel> updateDino(DinoDTO model) throws DinoNotFoundException {
        Optional<DinoModel> dinoModel = dinoRepository.findById(model.getId());
        LocationModel location;
        if(model.getLocationId() != null) {
            location = locationRepository.findById(model.getLocationId()).get();
        } else {
            location = dinoModel.get().getLocation();
        }
        if(!dinoModel.isPresent()) throw new DinoNotFoundException(model.getId());
        DinoTypeModel type;
        if(model.getType() != null){
            type = dinoTypeRepository.findFirstByType(model.getType());
        } else {
            type = dinoModel.get().getType();
        }
        dinoRepository.save(new DinoModel(model, dinoModel.get(), location, type));
        dinoRepository.flush();
        return dinoModel;
    }

    public HttpStatus deleteDino(Integer dinoId) throws DinoNotFoundException {
        Optional<DinoModel> dinoModel = dinoRepository.findById(dinoId);
        if(dinoModel.isPresent()){
            dinoModel.get().setIsActive(Boolean.FALSE);
            dinoRepository.save(dinoModel.get());
            return HttpStatus.OK;
        }
        throw new DinoNotFoundException(dinoId);
    }

    public Optional<DinoModel> getDinoById(Integer dinoId){
        return dinoRepository.findById(dinoId);
    }
}
