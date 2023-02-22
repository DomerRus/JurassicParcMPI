package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.ScheduleDTO;
import ru.itmo.park.model.dto.ScheduleTaskDTO;
import ru.itmo.park.model.dto.response.ScheduleResponseDTO;
import ru.itmo.park.model.entity.LocationModel;
import ru.itmo.park.model.entity.ScheduleModel;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.repository.ScheduleRepository;
import ru.itmo.park.security.jwt.JwtProvider;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final UserService userService;

    private final JwtProvider jwtProvider;

    private final LocationService locationService;

    private final Random random = new Random();

    public Optional<List<ScheduleTaskDTO>> setUserSchedules(ScheduleDTO scheduleDTO) throws UserNotFoundException {
        UserModel userModel = userService.findById(scheduleDTO.getUserId()).get();
        AtomicInteger count = new AtomicInteger(0);
        scheduleDTO.getSchedules().stream()
                .sorted(Comparator.comparing(ScheduleTaskDTO::getDateTime))
                .forEach(item -> {
                    LocationModel locationModel = locationService.getLocationById(item.getLocationId()).get();
                    try {
                        Date date = new SimpleDateFormat("MM-dd-yy'T'HH:mm").parse(item.getDateTime());
                        Instant reqInstant = date.toInstant();
                        ScheduleModel model = ScheduleModel.builder()
                                .user(userModel)
                                .task(item.getTask())
                                .dateTime(reqInstant)
                                .location(locationModel)
                                .orderId(count.getAndIncrement())
                                .build();
                        scheduleRepository.save(model);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
        return Optional.of(scheduleDTO.getSchedules());
    }

    public Optional<List<ScheduleResponseDTO>> getSchedulesByUserId(String token, Integer userId, String dateTime){
        if(userId == null) userId = jwtProvider.getCurrentUser(token);
        Instant reqInstant = Instant.now();
        if (dateTime != null) {
            try {
                Date date = new SimpleDateFormat("MM-dd-yy").parse(dateTime);
                reqInstant = date.toInstant();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            LocalDate localDateTime = LocalDate.now();
            reqInstant = localDateTime.atStartOfDay(ZoneId.of("Europe/Moscow")).toInstant();
        }
        List<ScheduleResponseDTO> list = scheduleRepository.findAllByUser_IdAndDateTimeGreaterThanEqual(userId, reqInstant).get().stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());
        return Optional.of(list);
    }

    public Optional<List<ScheduleTaskDTO>> getInspectorSchedule(String token) throws UserNotFoundException {
        Integer userId = jwtProvider.getCurrentUser(token);
        LocalDate localDateTime = LocalDate.now();
        Instant reqInstant = localDateTime.atStartOfDay(ZoneId.of("Europe/Moscow")).toInstant();
        Optional<List<ScheduleModel>> list = scheduleRepository.findAllByDateTimeGreaterThan(reqInstant);
        ScheduleDTO modelDTOList = ScheduleDTO.builder()
                .userId(userId)
                .schedules(new ArrayList<>())
                .build();
        try {
            if (list.isPresent()) {
                Map<Integer, List<ScheduleModel>> map = list.get().stream().collect(Collectors.groupingBy(ScheduleModel::getOrderId));
                map.keySet().forEach(item -> {
                    List<ScheduleModel> group = map.get(item);
                    ScheduleModel model = group.get(getRandomNumberInts(group.size()-1));
                    Date myDate = Date.from(model.getDateTime());
                    String dateTime = new SimpleDateFormat("MM-dd-yy'T'HH:mm").format(myDate);
                    ScheduleTaskDTO modelDto = ScheduleTaskDTO.builder()
                            .locationId(model.getLocation().getId())
                            .task(model.getTask())
                            .dateTime(dateTime)
                            .build();
                    modelDTOList.getSchedules().add(modelDto);
                });
                return setUserSchedules(modelDTOList);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public int getRandomNumberInts(int max){
        int randomm = random.ints(0,(max+1)).findFirst().getAsInt();
        log.info("{}", randomm);
        return randomm;
    }
    public Optional<List<UserModel>> getUsersFreSchedule(){
        LocalDate localDateTime = LocalDate.now().minusDays(1);
        Instant reqInstant = localDateTime.atStartOfDay(ZoneId.of("Europe/Moscow")).toInstant();
        return userService.getUsersFreSchedule(reqInstant);
    }
}
