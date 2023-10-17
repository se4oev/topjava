package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository("byUser")
public class InMemoryMealByUserRepository implements MealRepository {

    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(m -> save(m.getUserId(), m));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            Map<Integer, Meal> userMeals = repository.computeIfAbsent(userId, id -> new ConcurrentHashMap<>());
            userMeals.put(meal.getId(), meal);
            return meal;
        }

        Map<Integer, Meal> userMeals = repository.computeIfPresent(userId, (id, meals) -> meals);

        return userMeals != null && (userMeals.computeIfPresent(meal.getId(), (integer, oldMeal) -> meal) != null)
                ? meal
                : null;
    }

    @Override
    public boolean delete(int userId, int id) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals != null && meals.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int mealId) {
        Map<Integer, Meal> meals = repository.computeIfAbsent(userId, integer -> new ConcurrentHashMap<>());
        Meal meal = meals.get(mealId);
        return meal.getUserId() == userId ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterByDate(userId, null, null);
    }

    @Override
    public List<Meal> filterByDate(int userId, LocalDate dateFrom, LocalDate dateTo) {
        return repository.computeIfAbsent(userId, id -> new ConcurrentHashMap<>()).values().stream()
                .filter(meal -> DateTimeUtil.isBetweenInclusive(meal.getDate(), dateFrom, dateTo))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }
}
