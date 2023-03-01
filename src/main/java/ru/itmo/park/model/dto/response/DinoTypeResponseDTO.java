package ru.itmo.park.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.park.model.entity.DinoTypeModel;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DinoTypeResponseDTO {
    Integer id;
    String type;

    public DinoTypeResponseDTO(DinoTypeModel model){
        this.id = model.getId();
        this.type = model.getType();
    }
}
