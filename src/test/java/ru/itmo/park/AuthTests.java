package ru.itmo.park;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.model.dto.TokenDTO;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.dto.UserLoginDTO;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthTests {

    @Autowired
    private UserService userService;

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
    void testSuccessfulAuth(){

        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("testuser@dino.ru")
                .password("admin1")
                .build();
        Optional<TokenDTO> token = userService.authenticate(loginDTO);
        assertFalse(token.isEmpty());
    }

    @Test
    void testUnsuccessfulAuth(){
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("testuser@dino.ru")
                .password("admin2")
                .build();
        Optional<TokenDTO> token = userService.authenticate(loginDTO);
        assertTrue(token.isEmpty());
    }

    @AfterEach
    void tearDown() {
        UserModel user = userService.findByEmail("testuser@dino.ru");
        userService.deleteUser(user.getId());

    }
}
