package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.model.dto.TaskDTO;
import ru.itmo.park.model.entity.TaskModel;
import ru.itmo.park.model.entity.TaskStatusModel;
import ru.itmo.park.model.entity.TaskTypeModel;
import ru.itmo.park.service.TaskService;

import java.util.List;

@RequestMapping("/api/task")
@RestController
@RequiredArgsConstructor
public class TaskResource {

    private final TaskService taskService;

    //add new task permitAll
    @PostMapping
    public ResponseEntity<TaskModel> addNewTask(@RequestBody TaskDTO taskDTO){
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
    //confirm task and set idBusy = true
    @PostMapping("/confirm")
    public ResponseEntity<TaskModel> confirmTask(@RequestParam("taskId") Integer taskId){
        return taskService.confirmTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
    //end task and set isBusy = false
    @PostMapping("/end")
    public ResponseEntity<TaskModel> endTask(@RequestParam("taskId") Integer taskId){
        return taskService.endTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

}
