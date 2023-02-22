package ru.itmo.park.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginDTO {
    String email;
    String password;
}
