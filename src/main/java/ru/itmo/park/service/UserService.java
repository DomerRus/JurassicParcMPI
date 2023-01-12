package ru.itmo.park.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.park.exception.UserDuplicateException;
import ru.itmo.park.model.dto.UserDTO;
import ru.itmo.park.model.entity.LocationModel;
import ru.itmo.park.model.entity.RoleModel;
import ru.itmo.park.model.dto.TokenDTO;
import ru.itmo.park.model.dto.UserLoginDTO;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.repository.LocationRepository;
import ru.itmo.park.repository.RoleRepository;
import ru.itmo.park.repository.UserRepository;
import ru.itmo.park.security.jwt.JwtProvider;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userEntityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private JwtProvider jwtProvider;

    public Optional<List<UserModel>> findAllUsers(){
        return Optional.of(userEntityRepository.findAllByIdIsNotNullOrderById());
    }

    public Optional<TokenDTO> authenticate(UserLoginDTO authData){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        UserModel user = findByEmail(authData.getEmail());
        if(user != null && encoder.matches(authData.getPassword(), user.getPassword())){
            return Optional.of(TokenDTO.builder().token(jwtProvider.generateToken(user)).build());
        }
        return Optional.empty();
    }

    public Optional<UserModel> findById(Integer id) {
        return userEntityRepository.findById(id);
    }

    public UserModel findByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    public Optional<UserModel> addNewUser(UserDTO model) throws UserDuplicateException {
        UserModel userModel = userEntityRepository.findByEmail(model.getEmail());
        if(userModel != null){
            throw new UserDuplicateException("Пользователь уже существует");
        }
        RoleModel role = roleRepository.findByName(model.getRole());
        LocationModel location = locationRepository.findById(1).get();
        return Optional.of(userEntityRepository.save(new UserModel(model, role, location)));
    }

    public Optional<UserModel> updateUser(UserDTO model){
        UserModel user = userEntityRepository.getById(model.getId());
        RoleModel role;
        if(model.getRole() != null){
            role = roleRepository.findByName(model.getRole());
        } else {
            role = user.getRole();
        }
        UserModel userModel = userEntityRepository.save(new UserModel(model, user, role));
        return Optional.of(userModel);
    }

    public HttpStatus deleteUser(Integer userId){
        try{
            userEntityRepository.deleteById(userId);
            return HttpStatus.OK;
        } catch (Exception e){
            return HttpStatus.BAD_REQUEST;
        }
    }

    public Optional<UserModel> getUserByToken(String token){
        return findById(jwtProvider.getCurrentUser(token));
    }

    public Optional<List<UserModel>> getFreeUserByRole(String token, String role){
        RoleModel roleModel = roleRepository.findByName(role);
        return Optional.of(userEntityRepository.findAllByRoleAndIsBusy(roleModel, false));

    }

    public void updateUser(UserModel model){
        userEntityRepository.save(model);
    }

}
