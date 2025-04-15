package com.example.fin_monitor_app.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Категория транзакций.
 */
@Getter
public enum CategoryEnum {
    // Расходы
    FOOD(1, "Продукты питания"),
    TRANSPORT(2, "Транспорт"),
    HOUSING(3, "Жилье (аренда/ипотека)"),
    UTILITIES(4, "Коммунальные услуги"),
    HEALTH(5, "Здоровье и медицина"),
    EDUCATION(6, "Образование"),
    ENTERTAINMENT(7, "Развлечения"),
    CLOTHING(8, "Одежда и обувь"),
    ELECTRONICS(9, "Техника и электроника"),
    GIFTS(10, "Подарки"),
    RESTAURANTS(11, "Рестораны и кафе"),
    TRAVEL(12, "Путешествия"),
    SPORT(13, "Спорт"),
    BEAUTY(14, "Красота и уход"),
    PETS(15, "Домашние животные"),
    INSURANCE(16, "Страховки"),
    TAXES(17, "Налоги"),
    INVESTMENTS(18, "Инвестиции"),
    OTHER_EXPENSES(26, "Прочие расходы"),

    // Доходы
    SALARY(19, "Зарплата"),
    FREELANCE(20, "Фриланс"),
    DIVIDENDS(21, "Дивиденды"),
    PENSION(22, "Пенсия"),
    SOCIAL_BENEFITS(23, "Социальные выплаты"),
    DEBT_RETURN(24, "Возврат долга"),
    OTHER_INCOME(25, "Прочие доходы");

    private final int id;
    private final String label;

    CategoryEnum(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public static final List<CategoryEnum> INCOME_CATEGORIES = Stream.of(
            FOOD,
            TRANSPORT,
            HOUSING,
            UTILITIES,
            HEALTH,
            EDUCATION,
            ENTERTAINMENT,
            CLOTHING,
            ELECTRONICS,
            GIFTS,
            RESTAURANTS,
            TRAVEL,
            SPORT,
            BEAUTY,
            PETS,
            INSURANCE,
            TAXES,
            INVESTMENTS,
            OTHER_EXPENSES).collect(Collectors.toList());

    public static final List<CategoryEnum> EXPENSES_CATEGORIES = Stream.of(
            SALARY,
            FREELANCE,
            DIVIDENDS,
            PENSION,
            SOCIAL_BENEFITS,
            DEBT_RETURN,
            OTHER_INCOME).collect(Collectors.toList());

    public static CategoryEnum fromId(int id) {
        return Arrays.stream(values())
                .filter(category -> category.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный ID: " + id));
    }
}
