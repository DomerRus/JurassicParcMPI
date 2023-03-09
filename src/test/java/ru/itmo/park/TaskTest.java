package ru.itmo.park;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.*;
import ru.itmo.park.model.dto.response.TaskResponseDTO;
import ru.itmo.park.model.entity.TaskModel;
import ru.itmo.park.service.TaskService;
import ru.itmo.park.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TaskTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;


    private List<UserDTO> list = List.of(
            UserDTO.builder()
                    .email("testuser@dino.ru")
                    .password("admin1")
                    .firstName("Дмитрий")
                    .secondName("Петренко")
                    .middleName("Андреевич")
                    .role("Manager")
                    .age(23)
                    .build(),
            UserDTO.builder()
                    .email("testuser2@dino.ru")
                    .password("admin1")
                    .firstName("Дмитрий")
                    .secondName("Петренко")
                    .middleName("Андреевич")
                    .role("Manager")
                    .age(23)
                    .build(),
            UserDTO.builder()
                    .email("testuser3@dino.ru")
                    .password("admin1")
                    .firstName("Дмитрий")
                    .secondName("Петренко")
                    .middleName("Андреевич")
                    .role("Manager")
                    .age(23)
                    .build());

    @BeforeEach
    void initAll() {
        list.forEach(item -> {
            try {
                userService.addNewUser(item);
            } catch (UserDuplicateException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testAddTasks(){
        List<TaskDTO> list = List.of(
                TaskDTO.builder()
                .from(userService.findByEmail("testuser@dino.ru").getId())
                .to(userService.findByEmail("testuser2@dino.ru").getId())
                .comment("Test")
                .type(1)
                .build(),
                TaskDTO.builder()
                .from(userService.findByEmail("testuser@dino.ru").getId())
                .to(userService.findByEmail("testuser3@dino.ru").getId())
                .comment("Test")
                .type(2)
                .build());
        Optional<ListTaskDTO> response = taskService.addNewTask(list);
        assertTrue(response.isPresent());
    }

    @Test
    void testConfirmTask(){
        List<TaskDTO> list = List.of(
                TaskDTO.builder()
                        .from(userService.findByEmail("testuser@dino.ru").getId())
                        .to(userService.findByEmail("testuser2@dino.ru").getId())
                        .comment("Test")
                        .type(1)
                        .build(),
                TaskDTO.builder()
                        .from(userService.findByEmail("testuser@dino.ru").getId())
                        .to(userService.findByEmail("testuser3@dino.ru").getId())
                        .comment("Test")
                        .type(2)
                        .build());
        Optional<ListTaskDTO> response = taskService.addNewTask(list);
        assertTrue(response.isPresent());
        Optional<TokenDTO> token = userService.authenticate(UserLoginDTO.builder().email("testuser2@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        Optional<TaskResponseDTO> task = taskService.getLastTaskByUserId(token.get().getToken());
        taskService.confirmTaskById(task.get().getId());
        token = userService.authenticate(UserLoginDTO.builder().email("testuser3@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        task = taskService.getLastTaskByUserId(token.get().getToken());
        taskService.confirmTaskById(task.get().getId());
        token = userService.authenticate(UserLoginDTO.builder().email("testuser@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        Optional<List<TaskResponseDTO>> tasks = taskService.getSendTaskByUser(token.get().getToken(), null);
        assertTrue(tasks.isPresent());
        tasks.get().forEach(t -> assertEquals(2, t.getStatus().getId()));
    }

    @Test
    void testEndTask() throws UserNotFoundException {
        List<TaskDTO> list = List.of(
                TaskDTO.builder()
                        .from(userService.findByEmail("testuser@dino.ru").getId())
                        .to(userService.findByEmail("testuser2@dino.ru").getId())
                        .comment("Test")
                        .type(1)
                        .build(),
                TaskDTO.builder()
                        .from(userService.findByEmail("testuser@dino.ru").getId())
                        .to(userService.findByEmail("testuser3@dino.ru").getId())
                        .comment("Test")
                        .type(2)
                        .build());
        Optional<ListTaskDTO> response = taskService.addNewTask(list);
        assertTrue(response.isPresent());
        Optional<TokenDTO> token = userService.authenticate(UserLoginDTO.builder().email("testuser2@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        Optional<TaskResponseDTO> task = taskService.getLastTaskByUserId(token.get().getToken());
        taskService.endTaskById(task.get().getId());
        token = userService.authenticate(UserLoginDTO.builder().email("testuser3@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        task = taskService.getLastTaskByUserId(token.get().getToken());
        taskService.endTaskById(task.get().getId());
        token = userService.authenticate(UserLoginDTO.builder().email("testuser@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        Optional<List<TaskResponseDTO>> tasks = taskService.getSendTaskByUser(token.get().getToken(), null);
        assertTrue(tasks.isPresent());
        tasks.get().forEach(t -> assertEquals(3, t.getStatus().getId()));
    }

    @Test
    void testCancelTask() throws UserNotFoundException {
        List<TaskDTO> list = List.of(
                TaskDTO.builder()
                        .from(userService.findByEmail("testuser@dino.ru").getId())
                        .to(userService.findByEmail("testuser2@dino.ru").getId())
                        .comment("Test")
                        .type(1)
                        .build(),
                TaskDTO.builder()
                        .from(userService.findByEmail("testuser@dino.ru").getId())
                        .to(userService.findByEmail("testuser3@dino.ru").getId())
                        .comment("Test")
                        .type(2)
                        .build());
        Optional<ListTaskDTO> response = taskService.addNewTask(list);
        assertTrue(response.isPresent());
        Optional<TokenDTO> token = userService.authenticate(UserLoginDTO.builder().email("testuser2@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        Optional<TaskResponseDTO> task = taskService.getLastTaskByUserId(token.get().getToken());
        taskService.cancelTaskById(task.get().getId(), null);
        token = userService.authenticate(UserLoginDTO.builder().email("testuser3@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        task = taskService.getLastTaskByUserId(token.get().getToken());
        taskService.cancelTaskById(task.get().getId(), null);
        token = userService.authenticate(UserLoginDTO.builder().email("testuser@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        Optional<List<TaskResponseDTO>> tasks = taskService.getSendTaskByUser(token.get().getToken(), null);
        assertTrue(tasks.isPresent());
        tasks.get().forEach(t -> assertEquals(4, t.getStatus().getId()));
    }

    @AfterEach
    void tearDown() {
        Optional<TokenDTO> token = userService.authenticate(UserLoginDTO.builder().email("testuser@dino.ru").password("admin1").build());
        assertTrue(token.isPresent());
        Optional<List<TaskResponseDTO>> tasks = taskService.getSendTaskByUser(token.get().getToken(), null);
        tasks.ifPresent(taskModels -> taskService.disableTasksByGroupId(taskModels.get(0).getGroupId()));
        list.forEach(item -> userService.deleteUser(userService.findByEmail(item.getEmail()).getId()));
    }
}
