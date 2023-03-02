package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.service.UserService;

import java.util.List;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    //new user manager

    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers(){
        return userService.findAllUsers()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<UserModel> addNewUserModel(@RequestBody UserDTO model) throws UserDuplicateException {
        return userService.addNewUser(model)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    //delete user manager
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId){
        return ResponseEntity.status(userService.deleteUser(userId)).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Integer userId) throws UserNotFoundException {
        return userService.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PatchMapping
    public ResponseEntity<UserModel> updateUser(@RequestBody UserDTO model) throws UserNotFoundException {
        return userService.updateUser(model)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    //get user by token
    @GetMapping("/me")
    public ResponseEntity<UserModel> getUserById(@RequestHeader("Authorization") String token) throws UserNotFoundException {
        return userService.getUserByToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    //get free user by role
    @GetMapping("/free")
    public ResponseEntity<List<UserModel>> getFreeUser(@RequestHeader("Authorization") String token,
                                                       @RequestParam("role") String role){
        return userService.getFreeUserByRole(token, role)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleJsonMappingException(Exception ex) {
        JSONObject errorResponse = new JSONObject();
        String[] name = ex.getClass().getName().split("\\.");
        errorResponse.put("error", name[name.length-1]);
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(errorResponse.toString());
    }
}
