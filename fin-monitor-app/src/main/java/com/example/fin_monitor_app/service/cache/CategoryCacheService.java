package com.example.fin_monitor_app.service.cache;

import com.example.fin_monitor_app.entity.Category;
import com.example.fin_monitor_app.repository.CategoryRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис, кеширующий данные по категориям.
 */
@Service
@RequiredArgsConstructor
public class CategoryCacheService {

    private final CategoryRepository categoryRepository;

    private Map<Integer, Category> categoryMap;

    @PostConstruct
    void init() {
        categoryMap = categoryRepository.findAll().stream().collect(Collectors.toMap(Category::getId, Function.identity()));
    }

    public Category findById(int id) {
        return categoryMap.get(id);
    }
}
