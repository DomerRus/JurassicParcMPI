package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.itmo.park.model.dto.NotificationDTO;
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

    private final NotificationService notificationService;

    private final TaskRepository taskRepository;

    private final JwtProvider jwtProvider;

    public Optional<TaskModel> addNewTask(TaskDTO taskDTO){
        UserModel userFrom = userService.findById(taskDTO.getFrom()).orElse(new UserModel());
        UserModel userTo = userService.findById(taskDTO.getTo()).orElse(new UserModel());
        userFrom.setIsBusy(true);
        userTo.setIsBusy(true);
        TaskModel taskModel = TaskModel.builder()
                .from(userFrom)
                .to(userTo)
                .type(taskTypeRepository.getReferenceById(taskDTO.getType()))
                .status(taskStatusRepository.getReferenceById(taskDTO.getStatus()))
                .comment(taskDTO.getComment())
                .creationDate(LocalDateTime.now())
                .build();
        NotificationDTO notif = NotificationDTO.builder()
                .to(userTo.getId())
                .body(String.format("Пользователь %s запрашивает охрану", userFrom.getFirstName()))
                .isAlert(false)
                .header("Новая задача!")
                .build();
        notificationService.newNotification(notif);
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
        Integer userId = jwtProvider.getCurrentUser(token);
        return taskRepository.findFirstByFrom_IdOrTo_IdAndStatus_IdIsLessThan(userId, userId, 3);
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
        UserModel userTo = userService.findById(model.getTo().getId()).orElse(new UserModel());
        userTo.setIsBusy(false);
        UserModel userFrom = userService.findById(model.getFrom().getId()).orElse(new UserModel());
        userFrom.setIsBusy(false);
        userService.updateUser(userTo);
        userService.updateUser(userFrom);
        return Optional.of(taskRepository.save(model));
    }
}
