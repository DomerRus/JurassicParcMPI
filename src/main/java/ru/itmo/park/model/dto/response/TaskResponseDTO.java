package ru.itmo.park.model.dto.response;

import lombok.*;
import ru.itmo.park.model.entity.TaskModel;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TaskResponseDTO {
    private Integer id;
    private Long groupId;
    private UserResponseDTO from;
    private UserResponseDTO to;
    private TaskTypeResponseDTO type;
    private TaskStatusResponseDTO status;
    private String comment;
    private LocalDateTime creationDate;
    private Boolean isActive;

    public TaskResponseDTO(TaskModel model){
        this.id = model.getId();
        this.groupId = model.getGroupId();
        this.from = new UserResponseDTO(model.getFrom());
        this.to = new UserResponseDTO(model.getTo());
        this.type = new TaskTypeResponseDTO(model.getType());
        this.status = new TaskStatusResponseDTO(model.getStatus());
        this.comment = model.getComment();
        this.creationDate = model.getCreationDate();
        this.isActive = model.getIsActive();
    }
}
