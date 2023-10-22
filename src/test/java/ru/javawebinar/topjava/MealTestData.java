package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final Integer USER_MEAL_ID = START_SEQ + 2;
    public static final Integer ADMIN_MEAL_ID = START_SEQ + 5;
    public static  final List<Meal> userMeal = new ArrayList<Meal>() {{
        add(new Meal(LocalDateTime.of(2023, 10, 20, 10, 38, 46), "Завтрак", 500));
        add(new Meal(LocalDateTime.of(2023, 10, 20, 13, 38, 51), "Обед", 700));
        add(new Meal(LocalDateTime.of(2023, 10, 20, 18, 0, 1), "Ужин", 400));
    }};
    public static final List<Meal> adminMeal = new ArrayList<Meal>() {{
        add(new Meal(LocalDateTime.of(2023, 10, 20, 9, 3, 5), "Завтрак", 400));
        add(new Meal(LocalDateTime.of(2023, 10, 20, 13, 31), "Обед", 300));
        add(new Meal(LocalDateTime.of(2023, 10, 20, 20, 16, 1), "Хрючево", 1200));
    }};

    public static Meal duplicatedMeal() {
        return new Meal(LocalDateTime.of(2023, 10, 20, 10, 38, 46), "Завтрак", 500);
    }

    public static Meal newMeal() {
        return new Meal(LocalDateTime.of(2023, 10, 22, 10, 0), "Завтрак", 500);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().ignoringFields().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields().isEqualTo(expected);
    }
}
