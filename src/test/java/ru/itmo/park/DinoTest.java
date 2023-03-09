package ru.itmo.park;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.itmo.park.exception.DinoNotFoundException;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.model.dto.DinoDTO;
import ru.itmo.park.model.dto.ReportTrainDTO;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.dto.response.DinoResponseDTO;
import ru.itmo.park.model.entity.DinoModel;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.service.DinoService;
import ru.itmo.park.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DinoTest {

    @Autowired
    private UserService userService;
    @Autowired
    private DinoService dinoService;

    private List<UserDTO> listUser = List.of(
            UserDTO.builder()
                    .email("testuser@dino.ru")
                    .password("admin1")
                    .firstName("Дмитрий")
                    .secondName("Петренко")
                    .middleName("Андреевич")
                    .role("Manager")
                    .age(23)
                    .build(),
            UserDTO.builder()
                    .email("testuser2@dino.ru")
                    .password("admin1")
                    .firstName("Дмитрий")
                    .secondName("Петренко")
                    .middleName("Андреевич")
                    .role("Manager")
                    .age(23)
                    .build(),
            UserDTO.builder()
                    .email("testuser3@dino.ru")
                    .password("admin1")
                    .firstName("Дмитрий")
                    .secondName("Петренко")
                    .middleName("Андреевич")
                    .role("Manager")
                    .age(23)
                    .build());

    @BeforeEach
    void initAll() {
        listUser.forEach(item -> {
            try {
                userService.addNewUser(item);
            } catch (UserDuplicateException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testAddAndDeleteDino() throws DinoNotFoundException {
        DinoDTO dto = DinoDTO.builder()
                .name("TestDino1")
                .type("Дейноних")
                .weight(123)
                .height(123)
                .age("1")
                .build();
        Optional<DinoResponseDTO> responseDTO = dinoService.addNewDino(dto);
        assertTrue(responseDTO.isPresent());
        dinoService.deleteDino(responseDTO.get().getId());
    }

    @Test
    void testUpdateDino() throws DinoNotFoundException {
        DinoDTO dto = DinoDTO.builder()
                .name("TestDino1")
                .type("Дейноних")
                .weight(123)
                .height(123)
                .age("1")
                .build();
        Optional<DinoResponseDTO> responseDTO = dinoService.addNewDino(dto);
        assertTrue(responseDTO.isPresent());
        DinoDTO dtoUpdate = DinoDTO.builder()
                .id(responseDTO.get().getId())
                .name("DinoUpdate")
                .build();
        Optional<DinoResponseDTO> updated = dinoService.updateDino(dtoUpdate);
        assertTrue(updated.isPresent());
        assertEquals("DinoUpdate", updated.get().getName());
        dinoService.deleteDino(responseDTO.get().getId());
    }

    @Test
    void testHealthDino() throws DinoNotFoundException {
        DinoDTO dto = DinoDTO.builder()
                .name("TestDino1")
                .type("Дейноних")
                .weight(123)
                .height(123)
                .age("1")
                .build();
        Optional<DinoResponseDTO> responseDTO = dinoService.addNewDino(dto);
        assertTrue(responseDTO.isPresent());
        Optional<DinoResponseDTO> updated = dinoService.setHealthy(Boolean.FALSE, responseDTO.get().getId());
        assertTrue(updated.isPresent());
        assertEquals(Boolean.FALSE, updated.get().getIsHealthy());
        dinoService.deleteDino(responseDTO.get().getId());
    }

    @Test
    void testTrainDino() throws DinoNotFoundException {
        DinoDTO dto = DinoDTO.builder()
                .name("TestDino1")
                .type("Дейноних")
                .weight(123)
                .height(123)
                .age("1")
                .build();
        Optional<DinoResponseDTO> responseDTO = dinoService.addNewDino(dto);
        assertTrue(responseDTO.isPresent());
        DinoDTO dtoUpdate = DinoDTO.builder()
                .id(responseDTO.get().getId())
                .name("DinoUpdate")
                .calm(98)
                .training(99)
                .build();
        Optional<DinoResponseDTO> updated = dinoService.updateDino(dtoUpdate);
        assertTrue(updated.isPresent());
        assertEquals(99, updated.get().getTraining());
        assertEquals(98, updated.get().getCalm());
        dinoService.deleteDino(responseDTO.get().getId());
    }

    @Test
    void testRecommend() throws DinoNotFoundException {
        DinoDTO dto = DinoDTO.builder()
                .name("TestDino1")
                .type("Дейноних")
                .weight(123)
                .height(123)
                .age("1")
                .build();
        Optional<DinoResponseDTO> responseDTO = dinoService.addNewDino(dto);
        assertTrue(responseDTO.isPresent());
        Optional<List<DinoResponseDTO>> recommend = dinoService.getAllDinoRecommend();
        assertTrue(recommend.isPresent());
        assertFalse(recommend.get().stream().filter(item -> Objects.equals(item.getId(), responseDTO.get().getId())).collect(Collectors.toList()).get(0).getIsRecommend());
        DinoDTO dtoUpdate = DinoDTO.builder()
                .id(responseDTO.get().getId())
                .name("DinoUpdate")
                .calm(98)
                .training(99)
                .build();
        Optional<DinoResponseDTO> updated = dinoService.updateDino(dtoUpdate);
        assertTrue(updated.isPresent());
        assertEquals(99, updated.get().getTraining());
        assertEquals(98, updated.get().getCalm());
        recommend = dinoService.getAllDinoRecommend();
        assertTrue(recommend.isPresent());
        assertTrue(recommend.get().stream().filter(item -> Objects.equals(item.getId(), responseDTO.get().getId())).collect(Collectors.toList()).get(0).getIsRecommend());
        dinoService.deleteDino(responseDTO.get().getId());
    }

    @Test
    void testDinoNotFound() {
        assertThrows(DinoNotFoundException.class, ()-> dinoService.deleteDino(0));
    }

    @AfterEach
    void tearDown() {
        listUser.forEach(item -> userService.deleteUser(userService.findByEmail(item.getEmail()).getId()));
    }
}
