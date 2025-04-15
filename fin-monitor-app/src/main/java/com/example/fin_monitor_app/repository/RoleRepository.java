package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
