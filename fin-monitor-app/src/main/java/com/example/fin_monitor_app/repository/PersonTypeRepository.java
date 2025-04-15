package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonTypeRepository extends JpaRepository<PersonType, Long> {
}
