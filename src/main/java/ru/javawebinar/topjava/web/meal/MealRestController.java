package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;

@Controller
public class MealRestController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(getAuthenticatedUserId(), meal);
    }

    public Meal get(int mealId) {
        log.info("get {}", mealId);
        return service.get(getAuthenticatedUserId(), mealId);
    }

    public List<MealTo> getAll() {
        log.info("getAll");
        return service.getAll(getAuthenticatedUserId(), getAuthenticatedUserCalories());
    }

    public List<MealTo> filterByDateTime(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("filterByDateTime");
        return service.filterByDate(
                getAuthenticatedUserId(), startDate, endDate, startTime, endTime, getAuthenticatedUserCalories());
    }

    public void update(Meal meal, int id) {
        log.info("update {}", meal);
        assureIdConsistent(meal, id);
        service.update(getAuthenticatedUserId(), meal);
    }

    public void delete(int mealId) {
        log.info("delete {}", mealId);
        service.delete(getAuthenticatedUserId(), mealId);
    }

    private int getAuthenticatedUserId() {
        return SecurityUtil.authUserId();
    }

    private int getAuthenticatedUserCalories() {
        return SecurityUtil.authUserCaloriesPerDay();
    }
}