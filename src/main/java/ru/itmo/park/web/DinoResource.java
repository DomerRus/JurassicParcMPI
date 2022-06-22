package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.model.dto.ReportDTO;
import ru.itmo.park.model.entity.DinoModel;
import ru.itmo.park.model.entity.ReportModel;
import ru.itmo.park.service.DinoService;

import java.util.List;

@RequestMapping("/api/dino")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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

    //dino report (add notification)
    @PostMapping("/report")
    public ResponseEntity<ReportModel> sendReport(@RequestHeader("Authorization") String token,
                                                  @RequestBody ReportDTO reportDTO){
        return dinoService.sendReport(token, reportDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    //dino isHealthy = false by id (add notification)
    @PostMapping("/healthy")
    public ResponseEntity<DinoModel> setHealthy(@RequestParam("healthy") Boolean healthy,
                                                @RequestParam("dinoId")Integer dinoId){
        return dinoService.setHealthy(healthy,dinoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
    //---
}
