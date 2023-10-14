package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;

@Controller
public class MealRestController {

    @Autowired
    private MealService service;

    public Meal create(Meal meal) {
        int userId = getAuthenticatedUserId();
        return service.create(userId, meal);
    }

    public Meal get(int mealId) {
        int userId = getAuthenticatedUserId();
        return service.get(userId, mealId);
    }

    public List<MealTo> getAll() {
        return MealsUtil.getTos(service.getAll(), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public void update(Meal meal) {
        int userId = getAuthenticatedUserId();
        service.update(userId, meal);
    }

    public void delete(int mealId) {
        int userId = getAuthenticatedUserId();
        service.delete(userId, mealId);
    }

    private int getAuthenticatedUserId() {
        return SecurityUtil.authUserId();
    }
}