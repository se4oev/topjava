package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;

    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("userId", userId)
                .addValue("dateTime", meal.getDateTime())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories());
        if (meal.isNew()) {
            Number newKey = insertMeal.executeAndReturnKey(map);
            meal.setId(newKey.intValue());
            return meal;
        }
        String updateQuery = "UPDATE meals " +
                "                SET date_time = :dateTime" +
                "                   ,description = :description" +
                "                   ,calories = :calories " +
                "              WHERE id = :id" +
                "                AND user_id = :userId ";
        int affectedRows = namedParameterJdbcTemplate.update(updateQuery, map);
        return affectedRows == 0 ? null : meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        String deleteQuery = "DELETE FROM meals " +
                "              WHERE id = ? " +
                "                AND user_id = ? ";
        return jdbcTemplate.update(deleteQuery, id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        String selectQuery = "SELECT * " +
                "               FROM meals m " +
                "              WHERE m.id = ? " +
                "                AND m.user_id = ? ";
        List<Meal> meals = jdbcTemplate.query(selectQuery, ROW_MAPPER, id, userId);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        String selectQuery = "SELECT * " +
                "               FROM meals m " +
                "              WHERE m.user_id = ? " +
                "              ORDER BY m.date_time DESC ";
        return jdbcTemplate.query(selectQuery, ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        String filterQuery = "SELECT * " +
                "               FROM meals m " +
                "              WHERE m.user_id = ? " +
                "                AND m.date_time >= ? " +
                "                AND m.date_time < ? ";
        return jdbcTemplate.query(filterQuery, ROW_MAPPER, userId, startDateTime, endDateTime);
    }
}
