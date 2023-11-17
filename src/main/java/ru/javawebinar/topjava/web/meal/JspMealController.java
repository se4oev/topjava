package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController {

    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    private MealService mealService;

    @GetMapping
    public String meals(HttpServletRequest request, Model model) {
        log.info("meals");

        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete" -> {
                int id = getId(request);
                mealService.delete(id, authUserId());
                return "redirect:meals";
            }
            case "create", "update" -> {
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        mealService.get(getId(request), authUserId());
                model.addAttribute("meal", meal);
                return "mealForm";
            }
            case "filter" -> {
                LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
                LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
                LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
                LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
                List<MealTo> mealToList = MealsUtil.getFilteredTos(mealService.getBetweenInclusive(
                        startDate, endDate, authUserId()), SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
                model.addAttribute("meals", mealToList);
                return "meals";
            }
            default -> {
                model.addAttribute("meals", MealsUtil.getTos(
                        mealService.getAll(authUserId()), SecurityUtil.authUserCaloriesPerDay()));
                return "meals";
            }
        }
    }

    @PostMapping
    public String saveMeal(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                request.getParameter("id") == null ? null : getId(request),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            mealService.update(meal, authUserId());
        } else {
            mealService.create(meal, authUserId());
        }
        return "redirect:meals";
    }

    public int authUserId() {
        return SecurityUtil.authUserId();
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
