package ru.itmo.park.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.itmo.park.model.entity.LocationModel;
import ru.itmo.park.model.entity.ScheduleModel;
import ru.itmo.park.model.entity.UserModel;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAmount;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ScheduleResponseDTO {
    private Integer id;
    private UserResponseDTO user;
    private Instant dateTime;
    private String task;
    private LocationResponseDTO location;
    private Integer orderId;

    public ScheduleResponseDTO(ScheduleModel model){
        this.id = model.getId();
        this.dateTime = model.getDateTime().plusSeconds(10800);
        this.user = new UserResponseDTO(model.getUser());
        this.task = model.getTask();
        this.location = new LocationResponseDTO(model.getLocation());
        this.orderId = model.getOrderId();
    }
}
