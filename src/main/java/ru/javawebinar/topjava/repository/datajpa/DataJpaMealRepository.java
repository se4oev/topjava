package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private static final Sort SORT_DATETIME = Sort.by(Sort.Direction.DESC, "dateTime");

    private final CrudMealRepository crudRepository;
    @PersistenceContext
    private EntityManager em;

    public DataJpaMealRepository(CrudMealRepository crudRepository,
                                 EntityManager em) {
        this.crudRepository = crudRepository;
        this.em = em;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(em.getReference(User.class, userId));
        if (meal.isNew()) {
            return crudRepository.save(meal);
        }
        return get(meal.id(), userId) == null
                ? null
                : crudRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.findOneByIdAndUserId(id, userId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAllByUserId(userId, SORT_DATETIME);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.getBetweenHalfOpen(userId, startDateTime, endDateTime);
    }
}
