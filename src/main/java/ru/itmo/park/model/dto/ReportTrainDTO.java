package ru.itmo.park.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReportTrainDTO {
    @JsonProperty("dino_id")
    Integer dinoId;
    Integer training;
    Integer calm;
}
