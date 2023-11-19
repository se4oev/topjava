package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.service.MealService;

@Controller
public class RestMealController extends MealController {

    public RestMealController(MealService service) {
        super(service);
    }
}