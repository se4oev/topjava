package ru.javawebinar.topjava.service;

import org.junit.Assert;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
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
        Meal created = mealService.create(newMeal(), 100000);
        Integer newId = created.getId();
        Meal newMeal = newMeal();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(mealService.get(newId, 100000), newMeal);
    }

    @Test(expected = DataAccessException.class)
    public void duplicateDateTimeOnOneUser() {
        mealService.create(duplicatedMeal(), USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void delete() {
        Meal meal = mealService.get(USER_MEAL_ID, USER_ID);
        assertNotNull(meal);
        mealService.delete(USER_MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> mealService.get(USER_MEAL_ID, USER_ID));
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() {
        mealService.delete(10, USER_ID);
    }

    @Test
    public void get() {
        Assert.fail();
    }

    @Test
    public void getNotFound() {
        Assert.fail();
    }

    @Test
    public void update() {
        Assert.fail();
    }

    @Test
    public void updateNotFound() {
        Assert.fail();
    }

    @Test
    public void getAll() {
        Assert.fail();
    }
}
