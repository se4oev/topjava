package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static ru.javawebinar.topjava.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    private static final String EXCEPTION_DUPLICATE_EMAIL = "user.duplicateEmail";

    private static final Map<String, String> CONSTRAINS_I18N_MAP = Map.of(
            "users_unique_email_idx", EXCEPTION_DUPLICATE_EMAIL);

    //  http://stackoverflow.com/a/22358422/548473
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo notFoundError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(
                req, e, false, DATA_NOT_FOUND, () -> List.of(ValidationUtil.getRootCause(e).toString()));
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo validationError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(
                req, e, false, VALIDATION_ERROR, () -> List.of(ValidationUtil.getRootCause(e).toString()));
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(IllegalFieldsException.class)
    public ErrorInfo validationError(HttpServletRequest req, IllegalFieldsException e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, e::getFieldsErrors);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BindException.class)
    public ErrorInfo bindingError(HttpServletRequest req, BindException e) {
        return logAndGetErrorInfo(
                req, e, true, VALIDATION_ERROR, () -> ValidationUtil.getFieldErrors(e.getBindingResult()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo internalError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(
                req, e, true, APP_ERROR, () -> List.of(ValidationUtil.getRootCause(e).toString()));
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorInfo handleMethodArgumentNotValidException(HttpServletRequest req,
                                                           MethodArgumentNotValidException error) {
        return logAndGetErrorInfo(req, error, true, VALIDATION_ERROR,
                () -> ValidationUtil.getFieldErrors(error.getBindingResult()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo handleDataIntegrityViolationException(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMsg = ValidationUtil.getRootCause(e).getMessage();
        if (rootMsg != null) {
            String lowerCaseMsg = rootMsg.toLowerCase();
            for (Map.Entry<String, String> entry : CONSTRAINS_I18N_MAP.entrySet()) {
                if (lowerCaseMsg.contains(entry.getKey())) {
                    return logAndGetErrorInfo(req, e, true, VALIDATION_ERROR,
                            () -> List.of(messageSourceAccessor.getMessage(entry.getValue())));
                }
            }
        }
        return logAndGetErrorInfo(req, e, true, DATA_ERROR, List::of);
    }

    //    https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private static ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException,
                                                ErrorType errorType, Supplier<List<String>> errorsSupplier) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        if (logException) {
            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }
        return new ErrorInfo(req.getRequestURL(), errorType, errorType.message(), errorsSupplier.get());
    }
}