package com.example.fin_monitor_app.view;

import com.example.fin_monitor_app.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDto {
    private String login;
    private String name;
    private String email;
    private String phone;
    private String currentPassword;
    private String newPassword;


    public ProfileUpdateDto(User user) {
        this.login = user.getLogin();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}
