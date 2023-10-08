package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.ConcurentHashMapStorage;
import ru.javawebinar.topjava.storage.Storage;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.SequenceGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by karpenko on 06.10.2023.
 * Description:
 */
public class MealServlet extends HttpServlet {

    private static final Logger log = getLogger(MealServlet.class);

    private static final int CALORIES_PER_DAY = 2000;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm");

    private final Storage<Integer, Meal> mealStorage;

    public MealServlet() {
        this(new ConcurentHashMapStorage<>(() -> SequenceGenerator.nextId(Meal.class)));
    }

    public MealServlet(ConcurentHashMapStorage<Meal> mealStorage) {
        this.mealStorage = mealStorage;
        fillStorage(MealsUtil.generateMealsList());
    }

    private void fillStorage(List<Meal> meals) {
        meals.forEach(mealStorage::save);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        log.debug("get on /meals, action {}", action);

        if (action != null && (action.equals("insert") || action.equals("update"))) {
            String mealId = request.getParameter("mealId");
            if (mealId != null && !mealId.equals("0"))
                request.setAttribute("meal", mealStorage.findById(Integer.valueOf(mealId)));
            request.getRequestDispatcher("/meal.jsp").forward(request, response);
            return;
        }

        if (action != null && action.equalsIgnoreCase("delete")) {
            String mealId = request.getParameter("mealId");
            mealStorage.deleteById(Integer.valueOf(mealId));
            response.sendRedirect("/topjava/meals");
            return;
        }
        request.setAttribute("meals", filterMeals(mealStorage.findAll()));
        request.setAttribute("formatter", formatter);
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse responce) throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String dateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        String calories = request.getParameter("calories");
        String mealId = request.getParameter("mealId");
        Integer id = (mealId == null || mealId.isEmpty()) ? null : Integer.parseInt(mealId);
        Meal meal = new Meal(
                id,
                LocalDateTime.parse(dateTime.replace("T", " "), formatter),
                description,
                Integer.parseInt(calories)
        );
        mealStorage.save(meal);
        responce.sendRedirect("meals");
    }

    private Object filterMeals(List<Meal> meals) {
        return MealsUtil.filteredByStreams(
                meals,
                LocalTime.of(0, 0),
                LocalTime.of(23, 59, 59),
                CALORIES_PER_DAY
        );
    }

}
