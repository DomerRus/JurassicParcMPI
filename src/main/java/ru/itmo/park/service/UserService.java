package ru.itmo.park.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.exception.UserNotFoundException;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.dto.response.UserResponseDTO;
import ru.itmo.park.model.entity.LocationModel;
import ru.itmo.park.model.entity.RoleModel;
import ru.itmo.park.model.dto.TokenDTO;
import ru.itmo.park.model.dto.UserLoginDTO;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.repository.LocationRepository;
import ru.itmo.park.repository.RoleRepository;
import ru.itmo.park.repository.UserRepository;
import ru.itmo.park.security.jwt.JwtProvider;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userEntityRepository;

    private final RoleRepository roleRepository;

    private final LocationService locationService;

    private final JwtProvider jwtProvider;

    public Optional<List<UserModel>> findAllUsers(){
        return Optional.of(userEntityRepository.findAllByIdIsNotNullAndIsActiveOrderById(Boolean.TRUE));
    }

    public Optional<TokenDTO> authenticate(UserLoginDTO authData){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserModel user = findByEmail(authData.getEmail());
        if(user == null || !user.getIsActive()) return Optional.empty();
        if(encoder.matches(authData.getPassword(), user.getPassword())){
            return Optional.of(TokenDTO.builder().token(jwtProvider.generateToken(user)).build());
        }
        return Optional.empty();
    }

    public Optional<UserModel> findById(Integer id) throws UserNotFoundException {
        Optional<UserModel> user = userEntityRepository.findById(id);
        if(!user.isPresent()) throw new UserNotFoundException(id);
        return user;
    }

    public UserModel findByEmail(String email) {
        return userEntityRepository.findByEmailAndIsActive(email, true);
    }

    public Optional<UserModel> addNewUser(UserDTO model) throws UserDuplicateException {
        UserModel userModel = userEntityRepository.findByEmailAndIsActive(model.getEmail(), true);
        if(userModel != null){
            throw new UserDuplicateException(model.getEmail());
        }
        RoleModel role = roleRepository.findByName(model.getRole());
        LocationModel location = locationService.getLocationById(1).get();
        return Optional.of(userEntityRepository.save(new UserModel(model, role, location)));
    }

    public Optional<UserResponseDTO> updateUser(UserDTO model) throws UserNotFoundException {
        Optional<UserModel> user = userEntityRepository.findById(model.getId());
        if(!user.isPresent()) throw new UserNotFoundException(model.getId());
        RoleModel role;
        if(model.getRole() != null){
            role = roleRepository.findByName(model.getRole());
        } else {
            role = user.get().getRole();
        }
        userEntityRepository.save(new UserModel(model, user.get(), role));
        userEntityRepository.flush();
        user = userEntityRepository.findById(model.getId());
        return Optional.of(new UserResponseDTO(user.get()));
    }

    public HttpStatus deleteUser(Integer userId){
        Optional<UserModel> user = userEntityRepository.findById(userId);
        if(user.isPresent()) {
            user.get().setIsActive(Boolean.FALSE);
            userEntityRepository.save(user.get());
            return HttpStatus.OK;
        }
        return HttpStatus.BAD_REQUEST;
    }

    public Optional<UserModel> getUserByToken(String token) throws UserNotFoundException {
        return findById(jwtProvider.getCurrentUser(token));
    }

    public Optional<List<UserModel>> getFreeUserByRole(String token, String role){
        RoleModel roleModel = roleRepository.findByName(role);
        return Optional.of(userEntityRepository.findAllByRoleAndIsBusyAndIsActive(roleModel, false, true));

    }

    public Optional<List<UserModel>> getUsersFreSchedule(Instant date){
        return Optional.of(userEntityRepository.findUserFreeSchedule(date));
    }

    public void updateUser(UserModel model){
        userEntityRepository.save(model);
    }

}
