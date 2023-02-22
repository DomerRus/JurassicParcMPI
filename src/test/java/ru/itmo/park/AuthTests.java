package ru.itmo.park;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.itmo.park.model.dto.UserLoginDTO;
import ru.itmo.park.service.UserService;

//@SpringBootTest
class AuthTests {

//    @Autowired
    private UserService userService;

//    @Test
    void testSuccessfulAuth(){
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("medic@dino.ru")
                .password("admin1")
                .build();
    }

//    @Test
    void testUnsuccessfulAuth(){

    }
}
