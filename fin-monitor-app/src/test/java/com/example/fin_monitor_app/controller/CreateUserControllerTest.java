package com.example.fin_monitor_app.controller;

import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.UserCreationResult;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.CreateUserView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CreateUserController controller;

    //протестировать успешную регистрацию (создание) нового пользователя через контроллер
    @Test
    void registerUser_Success() {
        CreateUserView userView = new CreateUserView();
        userView.setLogin("test");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.createUser(userView))
                .thenReturn(UserCreationResult.successful(new User()));

        String result = controller.registerUser(userView, bindingResult, redirectAttributes);

        assertEquals("redirect:/registration", result);
        verify(redirectAttributes).addFlashAttribute("message", "Пользователь 'test' создан.");
    }
}
