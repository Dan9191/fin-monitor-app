package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.UserCreationResult;
import com.example.fin_monitor_app.repository.UserRepository;
import com.example.fin_monitor_app.view.CreateUserView;
import com.example.fin_monitor_app.view.ProfileUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис по работе с пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    /**
     * Создание пользователя.
     *
     * @param createUserView Представление пользователя.
     * @return Результат создания пользователя.
     */
    @Transactional
    public UserCreationResult createUser(CreateUserView createUserView) {
        log.info("Начало создания пользователя {}", createUserView.getLogin());
        // Проверка уникальности логина
        if (userRepository.existsByLogin(createUserView.getLogin())) {
            log.warn("Login {} already taken ", createUserView.getLogin());
            return UserCreationResult.failure("Логин уже занят");
        }
        User user = new User();
        user.setLogin(createUserView.getLogin());
        user.setPassword(passwordEncoder.encode(createUserView.getPassword()));
        user.setEmail(createUserView.getEmail());

        userRepository.save(user);
        log.info("User {} added successfully", createUserView.getLogin());
        return UserCreationResult.successful(user);
    }

    public void updateProfile(String login, ProfileUpdateDto updateDto) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Обновляем имя, если оно изменилось
        if (updateDto.getName() != null && !updateDto.getName().isEmpty()) {
            user.setName(updateDto.getName());
        }

        // Обновляем email, если он изменился
        if (updateDto.getEmail() != null && !updateDto.getEmail().isEmpty()) {
            user.setEmail(updateDto.getEmail());
        }

        // Обновляем пароль, если предоставлен текущий и новый
        if (updateDto.getCurrentPassword() != null &&
                updateDto.getNewPassword() != null &&
                !updateDto.getNewPassword().isEmpty()) {

            if (!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }

            user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
        }

        userRepository.save(user);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}
