package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;

import java.time.LocalDateTime;

@Repository
@Profile(Profiles.POSTGRES_DB)
public class PgJdbcMealRepository extends JdbcMealRepository<LocalDateTime> {

    public PgJdbcMealRepository(JdbcTemplate jdbcTemplate,
                                NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected LocalDateTime convertLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime;
    }
}
