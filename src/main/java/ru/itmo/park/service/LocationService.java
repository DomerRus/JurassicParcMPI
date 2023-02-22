package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.park.model.entity.LocationModel;
import ru.itmo.park.repository.LocationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;

    public Optional<List<LocationModel>> getLocation(){
        return Optional.of(locationRepository.findAll());
    }

    public Optional<LocationModel> getLocationById(Integer id){
        return locationRepository.findById(id);
    }
}
