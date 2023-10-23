package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final int NOT_FOUND = 10;

    public static final Meal userMeal1 = new Meal(START_SEQ + 3, LocalDateTime.of(2023, 10, 20, 10, 38, 46), "Завтрак", 500);
    public static final Meal userMeal2 = new Meal(START_SEQ + 4, LocalDateTime.of(2023, 10, 20, 13, 38, 51), "Обед", 700);
    public static final Meal userMeal3 = new Meal(START_SEQ + 5, LocalDateTime.of(2023, 10, 20, 18, 0, 1), "Ужин", 400);
    public static final Meal userMeal4 = new Meal(START_SEQ + 9, LocalDateTime.of(2023, 10, 21, 9, 20, 41), "Завтрак", 700);
    public static final Meal userMeal5 = new Meal(START_SEQ + 10, LocalDateTime.of(2023, 10, 21, 11, 40, 1), "Обед", 800);
    public static final Meal userMeal6 = new Meal(START_SEQ + 11, LocalDateTime.of(2023, 10, 21, 18, 20, 15), "Ужин", 900);

    public static final Meal adminMeal1 = new Meal(START_SEQ + 6, LocalDateTime.of(2023, 10, 20, 9, 3, 5), "Завтрак", 400);
    public static final Meal adminMeal2 = new Meal(START_SEQ + 7, LocalDateTime.of(2023, 10, 20, 13, 31), "Обед", 300);
    public static final Meal adminMeal3 = new Meal(START_SEQ + 8, LocalDateTime.of(2023, 10, 20, 20, 16, 1), "Хрючево", 1200);
    public static final Meal adminMeal4 = new Meal(START_SEQ + 12, LocalDateTime.of(2023, 10, 21, 8, 40, 5), "Завтрак", 300);
    public static final Meal adminMeal5 = new Meal(START_SEQ + 13, LocalDateTime.of(2023, 10, 21, 13, 57, 56), "Обед", 500);
    public static final Meal adminMeal6 = new Meal(START_SEQ + 14, LocalDateTime.of(2023, 10, 21, 21, 20, 1), "Вкуснятинка", 1500);

    public static Meal duplicatedUserMeal() {
        Meal duplicated = new Meal(userMeal1);
        duplicated.setId(null);
        return duplicated;
    }

    public static Meal getUpdatedAdminMeal() {
        Meal updated = new Meal(adminMeal1);
        updated.setCalories(450);
        updated.setDescription("Плотный завтрак");
        updated.setDateTime(LocalDateTime.of(2023, 10, 20, 8, 56, 5));
        return updated;
    }

    public static Meal newMeal() {
        return new Meal(LocalDateTime.of(2023, 10, 21, 10, 38, 46), "Завтрак", 600);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
