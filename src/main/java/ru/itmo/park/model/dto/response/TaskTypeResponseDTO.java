package ru.itmo.park.model.dto.response;

import lombok.*;
import ru.itmo.park.model.entity.TaskStatusModel;
import ru.itmo.park.model.entity.TaskTypeModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TaskTypeResponseDTO {
    Integer id;
    String type;

    public TaskTypeResponseDTO(TaskTypeModel model){
        this.id = model.getId();
        this.type = model.getType();
    }
}
