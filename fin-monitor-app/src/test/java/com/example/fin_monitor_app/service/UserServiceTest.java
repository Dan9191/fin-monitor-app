package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.UserCreationResult;
import com.example.fin_monitor_app.repository.UserRepository;
import com.example.fin_monitor_app.view.CreateUserView;
import com.example.fin_monitor_app.view.ProfileUpdateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    //протестировать создание нового пользователя
    @Test
    void createUser_Success() {
        CreateUserView userView = new CreateUserView();
        userView.setLogin("test");
        userView.setPassword("pass");

        when(userRepository.existsByLogin("test")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        UserCreationResult result = userService.createUser(userView);

        assertTrue(result.isCreated());
        verify(userRepository).save(any(User.class));
    }

    //протестировать смену пароля
    @Test
    void updateProfile_ChangesPassword() {
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setCurrentPassword("old");
        dto.setNewPassword("new");

        User user = new User();
        user.setPassword("encodedOld");

        when(userRepository.findByLogin("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "encodedOld")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("encodedNew");

        userService.updateProfile("user", dto);

        assertEquals("encodedNew", user.getPassword());
        verify(userRepository).save(user);
    }
}
