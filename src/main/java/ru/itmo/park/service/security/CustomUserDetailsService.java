package ru.itmo.park.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.itmo.park.model.entity.UserModel;
import ru.itmo.park.security.CustomUserDetails;
import ru.itmo.park.service.UserService;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userService.findByEmail(email);
        return CustomUserDetails.fromUserEntityToCustomUserDetails(user);
    }
}
