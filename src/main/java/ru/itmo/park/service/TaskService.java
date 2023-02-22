package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.ListTaskDTO;
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
import java.util.*;

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

    public Optional<ListTaskDTO> addNewTask(List<TaskDTO> list) {
        List<TaskModel> listTask = new ArrayList<>();
        Optional<Long> groupId = taskRepository.getTopByGroupId().map(aLong -> aLong + 1L).or(() -> Optional.of(1L));
        if(list.get(0).getGroupId() != null){
            List<TaskModel> canceled = taskRepository.findAllByFromIdAndCancel(list.get(0).getFrom());
            canceled.forEach(item->
            {
                item.setStatus(taskStatusRepository.getReferenceById(5));
                item.setIsActive(Boolean.FALSE);
                taskRepository.save(item);
            });
        }
        list.forEach(taskDTO -> {
            UserModel userFrom = null;
            try {
                userFrom = userService.findById(taskDTO.getFrom()).orElse(new UserModel());
            UserModel userTo = userService.findById(taskDTO.getTo()).orElse(new UserModel());
            userFrom.setIsBusy(true);
            userTo.setIsBusy(true);
            TaskModel taskModel = TaskModel.builder()
                    .groupId(taskDTO.getGroupId() == null ? groupId.get() : taskDTO.getGroupId())
                    .from(userFrom)
                    .to(userTo)
                    .type(taskTypeRepository.getReferenceById(taskDTO.getType()))
                    .status(taskStatusRepository.getReferenceById(1))
                    .comment(taskDTO.getComment())
                    .creationDate(LocalDateTime.now())
                    .isActive(Boolean.TRUE)
                    .build();
            NotificationDTO notif = NotificationDTO.builder()
                    .to(userTo.getId())
                    .body(String.format("Пользователь %s запрашивает охрану", userFrom.getFirstName()))
                    .isAlert(false)
                    .header("Новая задача!")
                    .build();
            notificationService.newNotification(notif);
            TaskModel model = taskRepository.save(taskModel);
            listTask.add(model);
            } catch (UserNotFoundException e) {
                log.error(e.getMessage());
            }
        });
        return Optional.of(ListTaskDTO.builder()
                        .tasks(listTask)
                        .groupId(groupId.get())
                        .build());
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
        return taskRepository.findFirstByTo_IdAndStatus_IdIsLessThan(userId, 3);
    }

    public Optional<List<TaskModel>> getSendTaskByUser(String token, Long groupId){
        Integer userId = jwtProvider.getCurrentUser(token);
        if (groupId != null ){
            return taskRepository.findAllByFromId(groupId);
        }
        return taskRepository.findAllByFromId(userId);
    }

    public Optional<TaskModel> confirmTaskById(Integer taskId) {
        TaskModel model = taskRepository.getReferenceById(taskId);
        model.setStatus(taskStatusRepository.getReferenceById(2));
        UserModel userModel = null;
        try {
            userModel = userService.findById(model.getTo().getId()).orElse(new UserModel());
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        userModel.setIsBusy(true);
        userService.updateUser(userModel);
        return Optional.of(taskRepository.save(model));
    }

    public Optional<TaskModel> endTaskById(Integer taskId) throws UserNotFoundException {
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

    public Optional<?> cancelTaskById(Integer taskId, Long groupId) throws UserNotFoundException {
        if (groupId == null){
            TaskModel model = taskRepository.getReferenceById(taskId);
            model.setStatus(taskStatusRepository.getReferenceById(4));
            UserModel userTo = userService.findById(model.getTo().getId()).orElse(new UserModel());
            userTo.setIsBusy(false);
            UserModel userFrom = userService.findById(model.getFrom().getId()).orElse(new UserModel());
            userFrom.setIsBusy(false);
            userService.updateUser(userTo);
            userService.updateUser(userFrom);
            NotificationDTO notif = NotificationDTO.builder()
                    .to(userFrom.getId())
                    .header("Задача отклонена!")
                    .body(String.format("Пользователь %s отказался от выполнения задачи.", userTo.getFirstName()))
                    .build();
            notificationService.newNotification(notif);
            return Optional.of(taskRepository.save(model));
        } else {
            Optional<List<TaskModel>> list = taskRepository.findAllByFromId(groupId);
            list.get().forEach(item->{
                item.setStatus(taskStatusRepository.getReferenceById(3));
                taskRepository.save(item);
            });
            return list;
         }
    }
    public Optional<TaskModel> resendTaskById(Integer taskId) throws UserNotFoundException {
        TaskModel model = taskRepository.getReferenceById(taskId);
        model.setStatus(taskStatusRepository.getReferenceById(5));
        UserModel userTo = userService.findById(model.getTo().getId()).orElse(new UserModel());
        userTo.setIsBusy(false);
        UserModel userFrom = userService.findById(model.getFrom().getId()).orElse(new UserModel());
        userFrom.setIsBusy(false);
        userService.updateUser(userTo);
        userService.updateUser(userFrom);
        NotificationDTO notif = NotificationDTO.builder()
                .to(userFrom.getId())
                .header("Задача отменена!")
                .body(String.format("Пользователь %s отказался от выполнения задачи.", userTo.getFirstName()))
                .build();
        notificationService.newNotification(notif);
        return Optional.of(taskRepository.save(model));
    }

    public HttpStatus disableTasksByGroupId(Long groupId) throws UserNotFoundException {
        try {
            List<TaskModel> tasks = taskRepository.tasksForDesyModel(groupId);
            tasks.forEach(taskModel -> {
                try {
                    UserModel userTo = userService.findById(taskModel.getTo().getId()).orElse(new UserModel());
                    userTo.setIsBusy(false);
                    userService.updateUser(userTo);
                    taskModel.setIsActive(Boolean.FALSE);
                    taskRepository.save(taskModel);
                } catch (UserNotFoundException e){
                    log.error(e.getMessage());
                }
            });
            UserModel userFrom = userService.findById(tasks.get(0).getFrom().getId()).orElse(new UserModel());
            userFrom.setIsBusy(false);
            userService.updateUser(userFrom);
            return HttpStatus.OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return HttpStatus.BAD_REQUEST;
        }
    }
}
