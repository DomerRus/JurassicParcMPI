package ru.itmo.park.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.park.model.entity.RoleModel;
import ru.itmo.park.model.dto.TokenDTO;
import ru.itmo.park.model.dto.UserLoginDTO;
import ru.itmo.park.model.entity.UserModel;
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
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public Optional<UserModel> addNewUser(UserModel model){
        return Optional.of(userEntityRepository.save(model));
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

    public Optional<UserModel> updateUser(UserModel model){
        return Optional.of(userEntityRepository.save(model));
    }
}
