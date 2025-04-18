package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.UserLoginResult;
import com.example.fin_monitor_app.repository.UserRepository;
import com.example.fin_monitor_app.view.LoginUserView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Возвращаем UserDetails без проверки пароля
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(), // Должен быть захэшированный пароль
                Collections.emptyList());
    }
}
