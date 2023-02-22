package ru.itmo.park.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.itmo.park.model.entity.LocationModel;
import ru.itmo.park.model.entity.RoleModel;
import ru.itmo.park.model.entity.UserModel;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserResponseDTO {
    Integer id;
    String email;
    String firstName;
    String secondName;
    String middleName;
    RoleResponseDTO role;
    Integer age;
    LocationResponseDTO location;
    Boolean isBusy;
    Boolean isActive;

    public UserResponseDTO(UserModel model){
        this.id = model.getId();
        this.email = model.getEmail();
        this.firstName = model.getFirstName();
        this.secondName = model.getSecondName();
        this.middleName = model.getMiddleName();
        this.role = new RoleResponseDTO(model.getRole());
        this.age = model.getAge();
        this.location = new LocationResponseDTO(model.getLocation());
        this.isBusy = model.getIsBusy();
        this.isActive = model.getIsActive();
    }
}
