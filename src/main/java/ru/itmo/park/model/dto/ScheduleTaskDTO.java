package ru.itmo.park.model.dto;

import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ScheduleTaskDTO {
    private String dateTime;
    private String task;
    private Integer locationId;
}
