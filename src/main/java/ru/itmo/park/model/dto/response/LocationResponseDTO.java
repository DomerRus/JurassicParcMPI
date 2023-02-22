package ru.itmo.park.model.dto.response;

import lombok.*;
import ru.itmo.park.model.entity.LocationModel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LocationResponseDTO {
    Integer id;
    String name;
    Double longitude;
    Double latitude;

    public LocationResponseDTO(LocationModel model){
        this.id = model.getId();
        this.latitude = model.getLatitude();
        this.longitude = model.getLongitude();
        this.name = model.getName();
    }
}
