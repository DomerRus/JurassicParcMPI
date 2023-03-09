package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.exception.DinoNotFoundException;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.DinoDTO;
import ru.itmo.park.model.dto.ReportDTO;
import ru.itmo.park.model.dto.ReportTrainDTO;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.dto.response.DinoResponseDTO;
import ru.itmo.park.model.entity.DinoModel;
import ru.itmo.park.model.entity.DinoTypeModel;
import ru.itmo.park.model.entity.ReportModel;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.service.DinoService;

import java.util.List;

@RequestMapping("/api/dino")
@RestController
@RequiredArgsConstructor
public class DinoResource {
    //---Medic report

    private final DinoService dinoService;

    //get dinos
    @GetMapping
    public ResponseEntity<List<DinoModel>> getDinos(){
        return dinoService.getAllDino()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<DinoResponseDTO>> getDinosRecommend(){
        return dinoService.getAllDinoRecommend()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    //dino report (add notification)
    @PostMapping("/report")
    public ResponseEntity<ReportModel> sendReport(@RequestHeader("Authorization") String token,
                                                  @RequestBody ReportDTO reportDTO) throws UserNotFoundException {
        return dinoService.sendReport(token, reportDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
    @PostMapping("/report/train")
    public ResponseEntity<ReportTrainDTO> sendReportTrain(@RequestBody ReportTrainDTO reportDTO) {
        return dinoService.sendReportTrain(reportDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    //dino isHealthy = false by id (add notification)
    @PostMapping("/healthy")
    public ResponseEntity<DinoResponseDTO> setHealthy(@RequestParam("healthy") Boolean healthy,
                                                @RequestParam("dinoId")Integer dinoId){
        return dinoService.setHealthy(healthy,dinoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping
    public ResponseEntity<DinoResponseDTO> addNewDinoModel(@RequestBody DinoDTO model) {
        return dinoService.addNewDino(model)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/type")
    public ResponseEntity<List<DinoTypeModel>> getAllTypes(){
        return dinoService.getAllType()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    //delete user manager
    @DeleteMapping("/{dinoId}")
    public ResponseEntity<Void> deleteDino(@PathVariable Integer dinoId) throws DinoNotFoundException {
        return ResponseEntity.status(dinoService.deleteDino(dinoId)).build();
    }

    @GetMapping("/{dinoId}")
    public ResponseEntity<DinoResponseDTO> getDinoById(@PathVariable Integer dinoId) {
        return dinoService.getDinoById(dinoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PatchMapping
    public ResponseEntity<DinoResponseDTO> updateDino(@RequestBody DinoDTO model) throws DinoNotFoundException {
        return dinoService.updateDino(model)
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
