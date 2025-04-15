package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
