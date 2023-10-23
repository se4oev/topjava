package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;

    @Test
    public void create() {
        Meal created = mealService.create(newMeal(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = newMeal();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test(expected = DataAccessException.class)
    public void createDuplicateDateTimeOnOneUser() {
        mealService.create(duplicatedUserMeal(), USER_ID);
    }

    @Test
    public void delete() {
        Meal meal = mealService.get(userMeal1.getId(), USER_ID);
        assertNotNull(meal);
        mealService.delete(userMeal1.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> mealService.get(userMeal1.getId(), USER_ID));
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() {
        mealService.delete(10, USER_ID);
    }

    @Test
    public void get() {
        Meal meal = mealService.get(userMeal1.getId(), USER_ID);
        assertMatch(meal, userMeal1);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() {
        mealService.get(NOT_FOUND, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getExistedButNotYourMeal() {
        mealService.get(userMeal1.getId(), ADMIN_ID);
    }

    @Test
    public void update() {
        Meal updated = getUpdatedAdminMeal();
        mealService.update(updated, ADMIN_ID);
        assertMatch(mealService.get(updated.getId(), ADMIN_ID), getUpdatedAdminMeal());
    }

    @Test
    public void updateNotFound() {
        Meal updated = getUpdatedAdminMeal();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> mealService.update(updated, ADMIN_ID));
    }

    @Test(expected = NotFoundException.class)
    public void updateExistedButNotYourFood() {
        mealService.update(getUpdatedAdminMeal(), USER_ID);
    }

    @Test
    public void getAll() {
        List<Meal> allUserMeals = mealService.getAll(USER_ID);
        assertMatch(allUserMeals, userMeal6, userMeal5, userMeal4, userMeal3, userMeal2, userMeal1);
    }
}
