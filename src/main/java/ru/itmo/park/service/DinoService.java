package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.park.model.dto.ReportDTO;
import ru.itmo.park.model.entity.DinoModel;
import ru.itmo.park.model.entity.ReportModel;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.repository.DinoRepository;
import ru.itmo.park.repository.DinoTypeRepository;
import ru.itmo.park.repository.ReportRepository;
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

    private final ReportRepository reportRepository;

    public Optional<List<DinoModel>> getAllDino(){
        return Optional.of(dinoRepository.findAll());
    }

    public Optional<ReportModel> sendReport(ReportDTO reportDTO){
        UserModel user = userService.findById(reportDTO.getUserId()).orElse(new UserModel());
        DinoModel dino = dinoRepository.getReferenceById(reportDTO.getDinoId());
        ReportModel report = ReportModel.builder()
                .age(reportDTO.getAge())
                .dino(dino)
                .isHealthy(reportDTO.getIsHealthy())
                .height(reportDTO.getHeight())
                .weight(reportDTO.getWeight())
                .user(user)
                .build();
        return Optional.of(reportRepository.save(report));
    }

    public Optional<DinoModel> setHealthy(Boolean healthy, Integer dinoId){
        DinoModel model = dinoRepository.getReferenceById(dinoId);
        model.setIsHealthy(healthy);
        return Optional.of(dinoRepository.save(model));
    }
}
