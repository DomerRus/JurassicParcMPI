package ru.itmo.park.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.park.model.dto.DinoDTO;
import ru.itmo.park.model.dto.ReportDTO;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "dinos")
public class DinoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateEagerInitializer", "hibernateLazyInitializer"})
    DinoTypeModel type;
    String age;
    Boolean isHealthy;
    Integer height;
    Integer training;
    Integer calm;
    Integer weight;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateEagerInitializer", "hibernateLazyInitializer"})
    LocationModel location;
    Boolean isActive;

    public DinoModel(DinoDTO dto, DinoTypeModel type, LocationModel location){
        this.name = dto.getName();
        this.age = dto.getAge();
        this.height = dto.getHeight();
        this.weight = dto.getWeight();
        this.isHealthy = true;
        this.location = location;
        this.type = type;
        this.training = 0;
        this.calm = 0;
        this.isActive = Boolean.TRUE;
    }
    public DinoModel(DinoDTO dto, DinoModel dino, LocationModel location, DinoTypeModel type){
        this.id = dto.getId();
        this.name = dto.getName() == null ? dino.getName() : dto.getName();
        this.age = dto.getAge() == null ? dino.getAge() : dto.getAge();
        this.height = dto.getHeight() == null ? dino.getHeight() : dto.getHeight();
        this.weight = dto.getWeight() == null ? dino.getWeight() : dto.getWeight();
        this.isHealthy = dino.getIsHealthy();
        this.location = location == null ? dino.getLocation() : location;
        this.type = type == null ? dino.getType() : type;
        this.training = dto.getTraining() == null ? dino.getTraining() : dto.getTraining();
        this.calm = dto.getCalm() == null ? dino.getCalm() : dto.getCalm();
        this.isActive = dino.isActive;
    }

    public DinoModel(ReportDTO dto, DinoModel dino){
        this.id = dto.getDinoId();
        this.name = dino.getName();
        this.age = dto.getAge() == null ? dino.getAge() : dto.getAge();
        this.height = dto.getHeight() == null ? dino.getHeight() : dto.getHeight();
        this.weight = dto.getWeight() == null ? dino.getWeight() : dto.getWeight();
        this.isHealthy = dino.getIsHealthy();
        this.location = dino.getLocation();
        this.type = dino.getType();
        this.training = dto.getTraining() == null ? dino.getTraining() : dto.getTraining();
        this.calm = dto.getCalm() == null ? dino.getCalm() : dto.getCalm();
        this.isActive = Boolean.TRUE;
    }
}
