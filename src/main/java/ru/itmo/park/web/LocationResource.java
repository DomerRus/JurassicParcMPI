package ru.itmo.park.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.park.model.entity.LocationModel;
import ru.itmo.park.service.LocationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
public class LocationResource {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationModel>> getLocation(){
        return locationService.getLocation()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
