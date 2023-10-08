package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MapStorage;
import ru.javawebinar.topjava.storage.Storage;
import ru.javawebinar.topjava.util.MapSequence;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
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
        this(new MapStorage<>(new MapSequence()));
    }

    public MealServlet(MapStorage<Meal> mealStorage) {
        this.mealStorage = mealStorage;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        fillStorage(MealsUtil.generateMealsList());
    }

    private void fillStorage(List<Meal> meals) {
        meals.forEach(mealStorage::save);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        log.debug("get on /meals, action {}", action);

        if ("edit".equals(action)) {
            String mealId = request.getParameter("mealId");
            if (mealId != null) {
                request.setAttribute("meal", mealStorage.findById(Integer.valueOf(mealId)));
            }
            request.getRequestDispatcher("/meal.jsp").forward(request, response);
            return;
        }

        if ("delete".equals(action)) {
            String mealId = request.getParameter("mealId");
            mealStorage.deleteById(Integer.valueOf(mealId));
            response.sendRedirect("meals");
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
                LocalDateTime.parse(dateTime),
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
                LocalTime.MAX,
                CALORIES_PER_DAY
        );
    }

}
