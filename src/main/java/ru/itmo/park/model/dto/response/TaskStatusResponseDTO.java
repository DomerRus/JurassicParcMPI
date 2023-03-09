package ru.itmo.park.model.dto.response;

import lombok.*;
import ru.itmo.park.model.entity.RoleModel;
import ru.itmo.park.model.entity.TaskStatusModel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TaskStatusResponseDTO {
    Integer id;
    String status;

    public TaskStatusResponseDTO(TaskStatusModel model){
        this.id = model.getId();
        this.status = model.getStatus();
    }
}
