package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(int userId, Meal meal) {
        return repository.save(userId, meal);
    }

    public void delete(int userId, int mealId) {
        checkNotFoundWithId(repository.delete(userId, mealId), mealId);
    }

    public Meal get(int userId, int mealId) {
        return checkNotFoundWithId(repository.get(userId, mealId), mealId);
    }

    public List<MealTo> getAll(int userId) {
        List<Meal> meals = repository.getAll(userId);
        return MealsUtil.getTos(meals, MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public void update(int userId, Meal meal) {
        checkNotFoundWithId(repository.save(userId, meal), meal.getId());
    }

    public List<MealTo> filterByDate(int userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        List<Meal> meals = repository.filterByDate(userId, startDate, endDate);
        return MealsUtil.getFilteredTos(meals, MealsUtil.DEFAULT_CALORIES_PER_DAY, startTime, endTime);
    }
}