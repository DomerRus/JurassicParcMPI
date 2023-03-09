package ru.itmo.park;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.*;
import ru.itmo.park.model.dto.response.ScheduleResponseDTO;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.service.ScheduleService;
import ru.itmo.park.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ScheduleTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleService scheduleService;

    @BeforeEach
    void initAll() throws UserDuplicateException {
        UserDTO user = UserDTO.builder()
                .email("testuser@dino.ru")
                .password("admin1")
                .firstName("Дмитрий")
                .secondName("Петренко")
                .middleName("Андреевич")
                .role("Manager")
                .age(23)
                .build();
        userService.addNewUser(user);
    }

    @Test
    void testAddSchedule() throws UserNotFoundException {
        Optional<List<UserModel>> listUser = userService.findAllUsers();
        assertTrue(listUser.isPresent());
        Integer userId = listUser.get().get(0).getId();
        ScheduleDTO schedule = ScheduleDTO.builder()
                .userId(userId)
                .schedules(List.of(
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'10.00")
                                .task("Test1")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'11.00")
                                .task("Test2")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'12.00")
                                .task("Test3")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'14.00")
                                .task("Test4")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'15.00")
                                .task("Test5")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'16.00")
                                .task("Test6")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'17.00")
                                .task("Test7")
                                .locationId(1)
                                .build())
                )
                .build();
        scheduleService.setUserSchedules(schedule);
        Optional<List<ScheduleResponseDTO>> list = scheduleService.getSchedulesByUserId(null, userId, null);
        assertTrue(list.isPresent());
    }

    @Test
    void testInspectorSchedule() throws UserDuplicateException, UserNotFoundException {
        UserModel user = userService.findByEmail("testuser@dino.ru");
        Integer userId = user.getId();
        ScheduleDTO schedule = ScheduleDTO.builder()
                .userId(userId)
                .schedules(List.of(
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'10.00")
                                .task("Test1")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'11.00")
                                .task("Test2")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'12.00")
                                .task("Test3")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'14.00")
                                .task("Test4")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'15.00")
                                .task("Test5")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'16.00")
                                .task("Test6")
                                .locationId(1)
                                .build(),
                        ScheduleTaskDTO.builder()
                                .dateTime("03-08-2023'T'17.00")
                                .task("Test7")
                                .locationId(1)
                                .build())
                )
                .build();
        scheduleService.setUserSchedules(schedule);
        Optional<List<ScheduleResponseDTO>> list = scheduleService.getSchedulesByUserId(null, userId, null);
        assertTrue(list.isPresent());
        UserDTO user2 = UserDTO.builder()
                .email("testuser2@dino.ru")
                .password("admin1")
                .firstName("Дмитрий")
                .secondName("Петренко")
                .middleName("Андреевич")
                .role("Inspector")
                .age(23)
                .build();
        Optional<UserModel> user3 = userService.addNewUser(user2);
        assertTrue(user3.isPresent());
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("testuser2@dino.ru")
                .password("admin1")
                .build();
        Optional<TokenDTO> token = userService.authenticate(loginDTO);
        assertFalse(token.isEmpty());
        Optional<List<ScheduleTaskDTO>> list2 = scheduleService.getInspectorSchedule(token.get().getToken());
        assertTrue(list2.isPresent());
        userService.deleteUser(user3.get().getId());
    }

    @AfterEach
    void tearDown() {
        UserModel user = userService.findByEmail("testuser@dino.ru");
        userService.deleteUser(user.getId());
    }
}
