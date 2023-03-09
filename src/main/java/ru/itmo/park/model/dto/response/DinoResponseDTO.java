package ru.itmo.park.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.park.model.entity.DinoModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DinoResponseDTO {
    Integer id;
    String name;
    DinoTypeResponseDTO type;
    String age;
    Boolean isHealthy;
    Integer height;
    Integer training;
    Integer calm;
    Integer weight;
    LocationResponseDTO location;
    Boolean isActive;

    Boolean isRecommend;

    public DinoResponseDTO setRecommend(Boolean recommend) {
        isRecommend = recommend;
        return this;
    }

    public DinoResponseDTO(DinoModel model){
        this.id = model.getId();
        this.name = model.getName();
        this.type = new DinoTypeResponseDTO(model.getType());
        this.age = model.getAge();
        this.isHealthy = model.getIsHealthy();
        this.height = model.getHeight();
        this.training = model.getTraining();
        this.calm = model.getCalm();
        this.weight = model.getWeight();
        this.location = new LocationResponseDTO(model.getLocation());
        this.isActive = model.getIsActive();
        this.isRecommend = Boolean.FALSE;
    }
}
