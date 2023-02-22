package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.ScheduleDTO;
import ru.itmo.park.model.dto.ScheduleTaskDTO;
import ru.itmo.park.model.dto.response.ScheduleResponseDTO;
import ru.itmo.park.model.entity.ScheduleModel;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.service.ScheduleService;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "*")
public class ScheduleResource {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<List<ScheduleTaskDTO>> setUserSchedule(@RequestBody ScheduleDTO model) throws UserNotFoundException {
        return scheduleService.setUserSchedules(model)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> getUserSchedule(@RequestHeader("Authorization") String token,
                                                                     @RequestParam(name = "userId", required = false) Integer userId,
                                                                     @RequestParam(name = "dateTime", required = false) String dateTime){
        return scheduleService.getSchedulesByUserId(token, userId, dateTime)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/free")
    public ResponseEntity<List<UserModel>> getUserFreeSchedule(){
        return scheduleService.getUsersFreSchedule()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/inspector")
    public ResponseEntity<List<ScheduleTaskDTO>> setInspectorSchedule(@RequestHeader("Authorization") String token) throws UserNotFoundException {
        return scheduleService.getInspectorSchedule(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
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
