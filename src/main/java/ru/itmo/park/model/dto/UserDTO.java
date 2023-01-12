package ru.itmo.park.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.park.model.entity.RoleModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    Integer id;
    String email;
    String firstName;
    String secondName;
    String middleName;
    String password;
    String role;
    Integer age;
}
