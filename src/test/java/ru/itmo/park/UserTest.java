package ru.itmo.park;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.dto.response.UserResponseDTO;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.repository.UserRepository;
import ru.itmo.park.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserTest {

    @Autowired
    private UserService userService;

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
    void testGetAllUser(){
        Optional<List<UserModel>> listUser = userService.findAllUsers();
        assertTrue(listUser.isPresent());
        listUser.get().forEach(item -> assertTrue(list.stream().map(UserDTO::getEmail).collect(Collectors.toList()).contains(item.getEmail())));
    }

    @Test
    void testUserCreateAndDelete() throws UserDuplicateException {
        UserDTO user = UserDTO.builder()
                .email("testuser4@dino.ru")
                .password("admin1")
                .firstName("Дмитрий")
                .secondName("Петренко")
                .middleName("Андреевич")
                .role("Worker")
                .age(23)
                .build();
        Optional<UserModel> newUser = userService.addNewUser(user);
        assertTrue(newUser.isPresent());
        assertEquals(newUser.get().getEmail(), user.getEmail());
        userService.deleteUser(userService.findByEmail(user.getEmail()).getId());
        assertNull(userService.findByEmail(user.getEmail()));
    }

    @Test
    void testUserDuplicate() {
        UserDTO user = UserDTO.builder()
                .email("testuser@dino.ru")
                .password("admin1")
                .firstName("Дмитрий")
                .secondName("Петренко")
                .middleName("Андреевич")
                .role("Worker")
                .age(23)
                .build();
        assertThrows(UserDuplicateException.class, ()-> userService.addNewUser(user));
    }

    @Test
    void testUserUpdate() throws UserNotFoundException {
        UserModel model = userService.findByEmail("testuser@dino.ru");
        UserDTO user = UserDTO.builder()
                .id(model.getId())
                .secondName("Греков")
                .build();
        Optional<UserResponseDTO> userUpdate = userService.updateUser(user);
        assertTrue(userUpdate.isPresent());
        assertEquals(userUpdate.get().getSecondName(), user.getSecondName());
    }

    @AfterEach
    void tearDown() {
        list.forEach(item -> userService.deleteUser(userService.findByEmail(item.getEmail()).getId()));
    }



}
