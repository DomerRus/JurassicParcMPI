package ru.itmo.park.model.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ScheduleDTO {
    private Integer userId;
    private List<ScheduleTaskDTO> schedules;
}
