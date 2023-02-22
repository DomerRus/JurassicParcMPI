package ru.itmo.park.model.dto.response;

import lombok.*;
import ru.itmo.park.model.entity.RoleModel;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RoleResponseDTO {
    private Long id;
    private String name;

    public RoleResponseDTO(RoleModel model){
        this.id = model.getId();
        this.name = model.getName();
    }
}
