package ru.itmo.park.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tasks_type")
public class TaskTypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String type;
}