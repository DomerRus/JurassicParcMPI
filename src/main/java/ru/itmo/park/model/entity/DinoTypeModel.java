package ru.itmo.park.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="dino_type")
public class DinoTypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String type;
}
