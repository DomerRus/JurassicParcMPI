package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.itmo.park.model.dto.TaskDTO;
import ru.itmo.park.model.entity.TaskModel;
import ru.itmo.park.model.entity.TaskStatusModel;
import ru.itmo.park.model.entity.TaskTypeModel;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.repository.TaskRepository;
import ru.itmo.park.repository.TaskStatusRepository;
import ru.itmo.park.repository.TaskTypeRepository;
import ru.itmo.park.security.jwt.JwtProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final UserService userService;

    private final TaskTypeRepository taskTypeRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final TaskRepository taskRepository;

    private final JwtProvider jwtProvider;

    public Optional<TaskModel> addNewTask(TaskDTO taskDTO){
        TaskModel taskModel = TaskModel.builder()
                .from(userService.findById(taskDTO.getFrom()).orElse(null))
                .to(userService.findById(taskDTO.getTo()).orElse(null))
                .type(taskTypeRepository.getReferenceById(taskDTO.getType()))
                .status(taskStatusRepository.getReferenceById(taskDTO.getStatus()))
                .comment(taskDTO.getComment())
                .creationDate(LocalDateTime.now())
                .build();
        return Optional.of(taskRepository.save(taskModel));
    }

    public Optional<List<TaskTypeModel>> getTaskTypes(){
        return Optional.of(taskTypeRepository.findAll());
    }

    public Optional<List<TaskStatusModel>> getTaskStatus(){
        return Optional.of(taskStatusRepository.findAll());
    }

    public Optional<List<TaskModel>> getTaskByUserId(String token){
        return taskRepository.findAllByTo_Id(jwtProvider.getCurrentUser(token));
    }

    public Optional<TaskModel> getLastTaskByUserId(String token){
        return taskRepository.findFirstByFrom_IdAndStatus_IdIsNot(jwtProvider.getCurrentUser(token), 3);
    }

    public Optional<TaskModel> confirmTaskById(Integer taskId){
        TaskModel model = taskRepository.getReferenceById(taskId);
        model.setStatus(taskStatusRepository.getReferenceById(2));
        UserModel userModel = userService.findById(model.getTo().getId()).orElse(new UserModel());
        userModel.setIsBusy(true);
        userService.updateUser(userModel);
        return Optional.of(taskRepository.save(model));
    }

    public Optional<TaskModel> endTaskById(Integer taskId){
        TaskModel model = taskRepository.getReferenceById(taskId);
        model.setStatus(taskStatusRepository.getReferenceById(3));
        UserModel userModel = userService.findById(model.getTo().getId()).orElse(new UserModel());
        userModel.setIsBusy(false);
        userService.updateUser(userModel);
        return Optional.of(taskRepository.save(model));
    }
}
