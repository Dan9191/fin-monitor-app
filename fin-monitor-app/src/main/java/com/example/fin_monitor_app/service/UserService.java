package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.UserCreationResult;
import com.example.fin_monitor_app.model.UserLoginResult;
import com.example.fin_monitor_app.repository.UserRepository;
import com.example.fin_monitor_app.service.cache.RoleCacheService;
import com.example.fin_monitor_app.view.CreateUserView;
import com.example.fin_monitor_app.view.LoginUserView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.fin_monitor_app.model.RoleEnum.USER;

/**
 * Сервис по работе с пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final RoleCacheService roleCacheService;

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
        user.setPassword(createUserView.getPassword());
        user.setRole(roleCacheService.findById(USER.getId()));

        userRepository.save(user);
        log.info("User {} added successfully", createUserView.getLogin());
        return UserCreationResult.successful(user);
    }

    public UserLoginResult loginUser(LoginUserView loginUser) {
        Optional<User> userJwt = userRepository.findByLoginAndPassword(loginUser.getLogin(), loginUser.getPassword());
        return userJwt.map(user -> UserLoginResult.successful())
                .orElseGet(() -> UserLoginResult.failure("Не удалось найти пользователя по такой комбинации"));
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
