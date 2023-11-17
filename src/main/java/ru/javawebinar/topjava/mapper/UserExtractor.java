package ru.javawebinar.topjava.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserExtractor implements ResultSetExtractor<List<User>> {

    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, User> map = new LinkedHashMap<>();
        while (rs.next()) {
            int userId = rs.getInt("id");
            String userName = rs.getString("name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            Date registered = rs.getDate("registered");
            boolean enabled = rs.getBoolean("enabled");
            int caloriesPerDay = rs.getInt("calories_per_day");
            String role = rs.getString("role");
            User user = map.computeIfAbsent(userId, key -> new User(
                    userId, userName, email, password, caloriesPerDay, enabled, registered, new ArrayList<>()));
            if (role != null) {
                user.getRoles().add(Role.valueOf(role));
            }
        }
        return new ArrayList<>(map.values());
    }
}
