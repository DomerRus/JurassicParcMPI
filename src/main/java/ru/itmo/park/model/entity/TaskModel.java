package ru.itmo.park.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tasks")
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Long groupId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateEagerInitializer", "hibernateLazyInitializer"})
    private UserModel from;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateEagerInitializer", "hibernateLazyInitializer"})
    private UserModel to;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateEagerInitializer", "hibernateLazyInitializer"})
    private TaskTypeModel type;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateEagerInitializer", "hibernateLazyInitializer"})
    private TaskStatusModel status;
    private String comment;
    private LocalDateTime creationDate;
    private Boolean isActive;

}
