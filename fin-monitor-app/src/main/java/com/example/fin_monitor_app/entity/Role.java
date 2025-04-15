package com.example.fin_monitor_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Сущность для хранения ссылки.
 */
@Entity
@Table(name = "role")
@NoArgsConstructor
@Getter
public class Role {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

}
