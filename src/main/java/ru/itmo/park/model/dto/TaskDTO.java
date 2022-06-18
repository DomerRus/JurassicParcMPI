package ru.itmo.park.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaskDTO {
    Integer from;
    Integer to;
    Integer type;
    Integer status;
    String comment;
}
