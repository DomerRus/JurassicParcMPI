package ru.itmo.park.model.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    String email;
    String password;
}
