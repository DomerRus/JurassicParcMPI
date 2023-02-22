package ru.itmo.park.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReportDTO {
    Integer dinoId;
    String age;
    Boolean isHealthy;
    Integer height;
    Integer weight;
    Integer training;
}
