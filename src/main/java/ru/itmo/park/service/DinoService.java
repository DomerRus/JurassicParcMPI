package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.itmo.park.exception.DinoNotFoundException;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.*;
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
        dino = new DinoModel(reportDTO, dino);
        dinoRepository.save(dino);
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

    public Optional<ReportTrainDTO> sendReportTrain(ReportTrainDTO reportDTO) {
        List<UserModel> users = userRepository.findAllByRole_Name("Manager");
        users.forEach(o->{
            NotificationDTO notif = NotificationDTO.builder()
                    .to(o.getId())
                    .header("Новый отчет!")
                    .body(String.format("Для дино с ИД %s требуется установить степень дрессированности = %s%% и степень спокойствия = %s%%.", reportDTO.getDinoId(), reportDTO.getTraining(), reportDTO.getCalm()))
                    .build();
            try {
                notificationService.newNotification(notif);
            } catch (UserNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return Optional.of(reportDTO);
    }

    public Optional<DinoResponseDTO> setHealthy(Boolean healthy, Integer dinoId){
        Optional<DinoModel> model = dinoRepository.findById(dinoId);
        if(model.isEmpty()) return Optional.empty();
        model.get().setIsHealthy(healthy);
        List<UserModel> users = userRepository.findAllByRole_Name("Manager");
        users.forEach(o->{
            NotificationDTO notif = NotificationDTO.builder()
                    .to(o.getId())
                    .header("Заболевание!")
                    .body(String.format("Заболел динозавр с id %s", model.get().getId()))
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
                    .body(String.format("Нужна транспортировка динозавра с id %s", model.get().getId()))
                    .build();
            try {
                notificationService.newNotification(notif);
            } catch (UserNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        dinoRepository.save(model.get());
        return Optional.of(new DinoResponseDTO(dinoRepository.findById(model.get().getId()).get()));
    }

    public Optional<DinoResponseDTO> addNewDino(DinoDTO model) {
        DinoTypeModel type = dinoTypeRepository.findFirstByType(model.getType());
        LocationModel location = locationRepository.findById(1).get();
        return Optional.of(new DinoResponseDTO(dinoRepository.save(new DinoModel(model, type, location))));
    }

    public Optional<DinoResponseDTO> updateDino(DinoDTO model) throws DinoNotFoundException {
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
        return Optional.of(new DinoResponseDTO(dinoRepository.findById(model.getId()).get()));
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

    public Optional<DinoResponseDTO> getDinoById(Integer dinoId){
        return Optional.of(new DinoResponseDTO(dinoRepository.findById(dinoId).orElse(new DinoModel())));
    }
}
