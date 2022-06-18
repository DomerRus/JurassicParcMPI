package ru.itmo.park.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.park.model.entity.DinoModel;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReportDTO {
    Integer dinoId;
    Integer userId;
    String age;
    Boolean isHealthy;
    Integer height;
    Integer weight;
}
