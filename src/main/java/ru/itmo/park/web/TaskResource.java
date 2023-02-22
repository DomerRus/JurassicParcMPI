package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.ListTaskDTO;
import ru.itmo.park.model.dto.TaskDTO;
import ru.itmo.park.model.entity.TaskModel;
import ru.itmo.park.model.entity.TaskStatusModel;
import ru.itmo.park.model.entity.TaskTypeModel;
import ru.itmo.park.service.TaskService;

import java.util.List;

@RequestMapping("/api/task")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskResource {

    private final TaskService taskService;

    //add new task permitAll
    @PostMapping
    public ResponseEntity<ListTaskDTO> addNewTask(@RequestBody List<TaskDTO> taskDTO) {
        return taskService.addNewTask(taskDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    //get task types
    @GetMapping("/types")
    public ResponseEntity<List<TaskTypeModel>> getTaskTypes(){
        return taskService.getTaskTypes()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    //get task status
    @GetMapping("/status")
    public ResponseEntity<List<TaskStatusModel>> getTaskStatus(){
        return taskService.getTaskStatus()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    //get tasks by token
    @GetMapping
    public ResponseEntity<List<TaskModel>> getTasksById(@RequestHeader("Authorization") String token){
        return taskService.getTaskByUserId(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    //get last task
    @GetMapping("/last")
    public ResponseEntity<TaskModel> getLastTask(@RequestHeader("Authorization") String token){
        return taskService.getLastTaskByUserId(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/send")
    public ResponseEntity<List<TaskModel>> getSendTask(@RequestHeader("Authorization") String token,
                                                       @RequestParam(name = "groupId", required = false) Long groupId){
        return taskService.getSendTaskByUser(token, groupId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    //confirm task and set idBusy = true
    @PostMapping("/confirm")
    public ResponseEntity<TaskModel> confirmTask(@RequestParam("taskId") Integer taskId){
        return taskService.confirmTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
    //end task and set isBusy = false
    @PostMapping("/end")
    public ResponseEntity<TaskModel> endTask(@RequestParam("taskId") Integer taskId) throws UserNotFoundException {
        return taskService.endTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelTask(@RequestParam(name = "taskId", required = false) Integer taskId, @RequestParam(name = "groupId", required = false) Long groupId) throws UserNotFoundException {
        return taskService.cancelTaskById(taskId, groupId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/resend")
    public ResponseEntity<TaskModel> resendTask(@RequestParam("taskId") Integer taskId) throws UserNotFoundException {
        return taskService.resendTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/disable")
    public ResponseEntity<Void> resendTask(@RequestParam("groupId") Long groupId) throws UserNotFoundException {
        return ResponseEntity.status(taskService.disableTasksByGroupId(groupId)).build();
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
