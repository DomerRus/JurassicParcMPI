package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.ListTaskDTO;
import ru.itmo.park.model.dto.NotificationDTO;
import ru.itmo.park.model.dto.TaskDTO;
import ru.itmo.park.model.dto.response.TaskResponseDTO;
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
import java.util.stream.Collectors;

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
                    .body(taskDTO.getComment())
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

    public Optional<List<TaskResponseDTO>> getTaskByUserId(String token){
        Optional<List<TaskModel>> tasks = taskRepository.findAllByTo_Id(jwtProvider.getCurrentUser(token));
        return tasks.map(taskModels -> taskModels.stream().map(TaskResponseDTO::new).collect(Collectors.toList()));
    }

    public Optional<TaskResponseDTO> getLastTaskByUserId(String token){
        Integer userId = jwtProvider.getCurrentUser(token);
        Optional<TaskModel> tasks = taskRepository.findFirstByTo_IdAndStatus_IdIsLessThan(userId, 3);
        return tasks.map(TaskResponseDTO::new);
    }

    public Optional<List<TaskResponseDTO>> getSendTaskByUser(String token, Long groupId){
        Integer userId = jwtProvider.getCurrentUser(token);
        Optional<List<TaskModel>> list;
        if (groupId != null ){
            list = taskRepository.findAllByFromId(groupId);
        } else {
            list = taskRepository.findAllByFromId(userId);
        }
        return list.map(taskModels -> taskModels.stream().map(TaskResponseDTO::new).collect(Collectors.toList()));
    }

    public Optional<TaskResponseDTO> confirmTaskById(Integer taskId) {
        Optional<TaskModel> model = taskRepository.findById(taskId);
        if(model.isEmpty()) return Optional.empty();
        model.get().setStatus(taskStatusRepository.getReferenceById(2));
        UserModel userModel = null;
        try {
            userModel = userService.findById(model.get().getTo().getId()).orElse(new UserModel());
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        userModel.setIsBusy(true);
        userService.updateUser(userModel);
        return Optional.of(new TaskResponseDTO(taskRepository.save(model.get())));
    }

    public Optional<TaskResponseDTO> endTaskById(Integer taskId) throws UserNotFoundException {
        Optional<TaskModel> model = taskRepository.findById(taskId);
        if(model.isEmpty()) return Optional.empty();
        model.get().setStatus(taskStatusRepository.getReferenceById(3));
        UserModel userTo = userService.findById(model.get().getTo().getId()).orElse(new UserModel());
        userTo.setIsBusy(false);
        UserModel userFrom = userService.findById(model.get().getFrom().getId()).orElse(new UserModel());
        userFrom.setIsBusy(false);
        userService.updateUser(userTo);
        userService.updateUser(userFrom);
        return Optional.of(new TaskResponseDTO(taskRepository.save(model.get())));
    }

    public Optional<?> cancelTaskById(Integer taskId, Long groupId) throws UserNotFoundException {
        if (groupId == null){
            Optional<TaskModel> model = taskRepository.findById(taskId);
            if(model.isEmpty()) return Optional.empty();
            model.get().setStatus(taskStatusRepository.getReferenceById(4));
            UserModel userTo = userService.findById(model.get().getTo().getId()).orElse(new UserModel());
            userTo.setIsBusy(false);
            UserModel userFrom = userService.findById(model.get().getFrom().getId()).orElse(new UserModel());
            userFrom.setIsBusy(false);
            userService.updateUser(userTo);
            userService.updateUser(userFrom);
            NotificationDTO notif = NotificationDTO.builder()
                    .to(userFrom.getId())
                    .header("Задача отклонена!")
                    .body(String.format("Пользователь %s отказался от выполнения задачи.", userTo.getFirstName()))
                    .build();
            notificationService.newNotification(notif);
            return Optional.of(taskRepository.save(model.get()));
        } else {
            Optional<List<TaskModel>> list = taskRepository.findAllByFromId(groupId);
            list.ifPresent(lt -> lt.forEach(item -> {
                item.setStatus(taskStatusRepository.getReferenceById(3));
                taskRepository.save(item);
            }));
            return list;
         }
    }
    public Optional<TaskResponseDTO> resendTaskById(Integer taskId) throws UserNotFoundException {
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
        return Optional.of(new TaskResponseDTO(taskRepository.save(model)));
    }

    public HttpStatus disableTasksByGroupId(Long groupId) {
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
