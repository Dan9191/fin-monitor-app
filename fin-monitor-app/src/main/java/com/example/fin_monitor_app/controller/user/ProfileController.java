package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.ProfileUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/account/profile")
@RequiredArgsConstructor
public class ProfileController {


    private final UserService userService;

    @GetMapping
    public String profilePage(Principal principal, Model model) {
        User user = userService.findByLogin(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("profileForm", new ProfileUpdateDto(user));
        return "account/profile";
    }

    @PostMapping
    public String updateProfile(
            @ModelAttribute("profileForm") ProfileUpdateDto profileForm,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            userService.updateProfile(principal.getName(), profileForm);
            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/account/profile";
    }
}
