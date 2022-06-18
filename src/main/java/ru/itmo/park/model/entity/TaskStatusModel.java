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
@Table(name = "task_status")
public class TaskStatusModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String status;
}
