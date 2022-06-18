package ru.itmo.park.web;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.park.model.dto.TokenDTO;
import ru.itmo.park.model.dto.UserLoginDTO;
import ru.itmo.park.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthResource {


    private final UserService userService;


    @PostMapping
    ResponseEntity<TokenDTO> auth(@RequestBody UserLoginDTO authData){
        return userService.authenticate(authData)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }


}
