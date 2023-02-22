package ru.itmo.park;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class ParkApplicationTests {

//    @Autowired
    private UserService userService;

//    @Test
    void testUserGet(){
        List<UserModel> users = userService.findAllUsers().get();
        assertFalse(users.isEmpty());
    }
    @SneakyThrows
//    @Test
    void testUserCreate(){
        UserDTO user = UserDTO.builder()
                .email("worker4@dino.ru")
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
        userService.deleteUser(newUser.get().getId());
    }

    @SneakyThrows
//    @Test
    void testUserDuplicate(){
        UserDTO user = UserDTO.builder()
                .email("test@dino.ru")
                .password("admin1")
                .firstName("Дмитрий")
                .secondName("Петренко")
                .middleName("Андреевич")
                .role("Worker")
                .age(23)
                .build();
        Optional<UserModel> newUser = userService.addNewUser(user);
        assertTrue(newUser.isPresent());
        assertThrows(UserDuplicateException.class, ()-> userService.addNewUser(user));
        userService.deleteUser(newUser.get().getId());
    }
}
