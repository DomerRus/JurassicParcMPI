package ru.itmo.park.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DinoDTO {
    Integer id;
    String name;
    String type;
    String age;
    Integer height;
    Integer weight;
    Integer locationId;
    Integer training;
    Integer calm;
}
