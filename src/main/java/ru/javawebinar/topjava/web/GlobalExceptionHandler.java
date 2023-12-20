package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolationException(HttpServletRequest req, DataIntegrityViolationException e) {
        log.error("Exception at request " + req.getRequestURL(), e);

        Throwable rootCause = ValidationUtil.getRootCause(e);
        String rootMsg = rootCause.getMessage();
        HttpStatus httpStatus = HttpStatus.CONFLICT;

        if (rootMsg != null) {
            if (rootMsg.toLowerCase().contains("users_unique_email_idx")) {
                ModelAndView modelAndView = new ModelAndView(
                        "profile",
                        Map.of(
                                "exception", rootCause,
                                "message", "User with this email already exists",
                                "status", httpStatus
                        )
                );
                return addUserToModelAndView(modelAndView);
            }
        }

        ModelAndView modelAndView = new ModelAndView(
                "profile",
                Map.of("exception", rootCause, "message", rootCause.toString(), "status", httpStatus)
        );
        modelAndView.setStatus(httpStatus);
        return addUserToModelAndView(modelAndView);
    }

    private ModelAndView addUserToModelAndView(ModelAndView modelAndView) {
        // Interceptor is not invoked, put userTo
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (authorizedUser != null) {
            modelAndView.addObject("userTo", authorizedUser.getUserTo());
        }
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        log.error("Exception at request " + req.getRequestURL(), e);
        Throwable rootCause = ValidationUtil.getRootCause(e);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ModelAndView mav = new ModelAndView("exception",
                Map.of("exception", rootCause, "message", rootCause.toString(), "status", httpStatus));
        mav.setStatus(httpStatus);

        return addUserToModelAndView(mav);
    }
}
