package ru.itmo.park.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.itmo.park.model.dto.UserDTO;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String email;
    String firstName;
    String secondName;
    String middleName;
    @JsonIgnore
    String password;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateEagerInitializer", "hibernateLazyInitializer"})
    RoleModel role;
    Integer age;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateEagerInitializer", "hibernateLazyInitializer"})
    LocationModel location;
    Boolean isBusy;
    Boolean isActive;

    public UserModel(UserDTO userDTO, RoleModel role, LocationModel location){
        this.age = userDTO.getAge();
        this.email = userDTO.getEmail();
        this.firstName = userDTO.getFirstName();
        this.middleName = userDTO.getMiddleName();
        this.secondName = userDTO.getSecondName();
        this.password = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        this.role = role;
        this.location = location;
        this.isBusy = false;
        this.isActive = Boolean.TRUE;
    }

    public UserModel(UserDTO userDTO, UserModel userModel, RoleModel role){
        this.id = userDTO.getId();
        this.age = userDTO.getAge() != null ? userDTO.getAge() : userModel.getAge();
        this.email = userDTO.getEmail() != null ? userDTO.getEmail() : userModel.getEmail();
        this.firstName = userDTO.getFirstName() != null ? userDTO.getFirstName() : userModel.getFirstName();
        this.middleName = userDTO.getMiddleName() != null ? userDTO.getMiddleName() : userModel.getMiddleName();
        this.secondName = userDTO.getSecondName() != null ? userDTO.getSecondName() : userModel.getSecondName();
        this.password = userDTO.getPassword() != null ? new BCryptPasswordEncoder().encode(userDTO.getPassword()) : userModel.getPassword();
        this.role = role;
        this.location = userModel.getLocation();
        this.isBusy = userModel.getIsBusy();
        this.isActive = userModel.isActive;
    }
}
