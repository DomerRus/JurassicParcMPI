package ru.itmo.park.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.park.model.entity.DinoTypeModel;
import ru.itmo.park.model.entity.LocationModel;

import javax.persistence.*;

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
}
