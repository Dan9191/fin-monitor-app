package com.example.fin_monitor_app.service.cache;

import com.example.fin_monitor_app.entity.Role;
import com.example.fin_monitor_app.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис, кеширующий данные по ролям.
 */
@Service
@RequiredArgsConstructor
public class RoleCacheService {

    private final RoleRepository roleRepository;

    private Map<Integer, Role> roles;

    @PostConstruct
    void init() {
        roles = roleRepository.findAll().stream().collect(Collectors.toMap(Role::getId, Function.identity()));
    }

    public Role findById(int id) {
        return roles.get(id);
    }

}
