package com.example.fin_monitor_app.service.cache;

import com.example.fin_monitor_app.entity.PersonType;
import com.example.fin_monitor_app.repository.PersonTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис, кеширующий данные по типу лица.
 */
@Service
@RequiredArgsConstructor
public class PersonTypeCacheService {

    private final PersonTypeRepository personTypeRepository;

    private Map<Integer, PersonType> personTypesMap;
    private List<PersonType> personTypesList;

    @PostConstruct
    void init() {
        personTypesList = personTypeRepository.findAll();
        personTypesMap = personTypesList.stream().collect(Collectors.toMap(PersonType::getId, Function.identity()));
    }

    public PersonType findById(int id) {
        return personTypesMap.get(id);
    }

    public List<PersonType> findAll() {
        return personTypesList;
    }
}
