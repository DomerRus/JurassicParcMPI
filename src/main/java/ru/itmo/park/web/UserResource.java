package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.service.UserService;

import java.util.List;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET}, exposedHeaders = {"Authorization"}, allowedHeaders = {"Authorization"})
public class UserResource {

    private final UserService userService;
    //new user manager
    @PostMapping("/save")
    public ResponseEntity<UserModel> addNewUser(@RequestBody UserModel model){
        return userService.addNewUser(model)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    //delete user manager
    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam Integer userId){
        return ResponseEntity.status(userService.deleteUser(userId)).build();
    }

    //update user manager
//    @PostMapping("/update")
//    public ResponseEntity<?> updateUser(@RequestParam Integer userId){
//        return userService.addNewUser(userId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.badRequest().build());
//    }

    //get user by token
    @GetMapping("/me")
    public ResponseEntity<UserModel> getUserById(@RequestHeader("Authorization") String token){
        return userService.getUserByToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    //get free user by role
    @GetMapping
    public ResponseEntity<List<UserModel>> getFreeUser(@RequestHeader("Authorization") String token,
                                                       @RequestParam("role") String role){
        return userService.getFreeUserByRole(token, role)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
