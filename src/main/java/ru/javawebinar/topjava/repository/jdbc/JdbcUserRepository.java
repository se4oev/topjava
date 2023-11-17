package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.mapper.UserExtractor;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            updateRoles(newKey.intValue(), user.getRoles());
        } else {
            String updateQuery = """
                    UPDATE users
                      SET name=:name
                         ,email=:email
                         ,password=:password
                         ,registered=:registered
                         ,enabled=:enabled
                         ,calories_per_day=:caloriesPerDay
                    WHERE id=:id
                    """;
            if (namedParameterJdbcTemplate.update(updateQuery, parameterSource) == 0) {
                return null;
            }
            jdbcTemplate.update("DELETE FROM user_role WHERE user_id = ?", user.getId());
            updateRoles(user.getId(), user.getRoles());
        }
        return user;
    }

    private void updateRoles(int id, Set<Role> roles) {
        String batchQuery = """
                    INSERT INTO user_role (user_id, role)
                    VALUES (?, ?)
                    """;
        jdbcTemplate.batchUpdate(batchQuery, roles, 200, (ps, role) -> {
            ps.setInt(1, id);
            ps.setString(2, role.name());
        });
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        String query = """
                SELECT *
                  FROM users u
                  LEFT JOIN user_role ur ON ur.user_id = u.id
                 WHERE u.id=?
                """;
        List<User> users = jdbcTemplate.query(query, new UserExtractor(), id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        String query = """
                SELECT *
                  FROM users u
                  LEFT JOIN user_role ur ON ur.user_id = u.id
                 WHERE u.email=?
                 """;
        List<User> users = jdbcTemplate.query(query, new UserExtractor(), email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        String query = """
                SELECT *
                  FROM users u
                  LEFT JOIN user_role ur ON ur.user_id = u.id
                 ORDER BY u.name, u.email
                 """;
        return jdbcTemplate.query(query, new UserExtractor());
    }
}
