package ru.practicum.explorewithme.utils;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.model.exception.AdminUpdateStatusException;
import ru.practicum.explorewithme.model.exception.BadRequestException;
import ru.practicum.explorewithme.model.exception.ConvertationException;
import ru.practicum.explorewithme.model.exception.FullEventException;
import ru.practicum.explorewithme.model.exception.ObjectNotFoundException;
import ru.practicum.explorewithme.model.exception.PermissionException;
import ru.practicum.explorewithme.model.exception.RequestCreationException;
import ru.practicum.explorewithme.model.exception.RequestStatusException;
import ru.practicum.explorewithme.model.exception.UserUpdateStatusException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            BadRequestException.class,
            ConvertationException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final Exception e) {
        log.error("Ошибка запроса: {}", e.getMessage(), e);
        return new ApiError(e.getMessage(),
                "Ошибка запроса",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now().format(CommonUtils.DATE_TIME_FORMATTER),
                getErrors(e));
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleObjectNotFound(final Exception e) {
        log.error("Запрашиваемый объект не найден {}", e.getMessage(), e);
        return new ApiError(e.getMessage(),
                "Запрашиваемый объект не найден",
                HttpStatus.NOT_FOUND.name(),
                LocalDateTime.now().format(CommonUtils.DATE_TIME_FORMATTER),
                getErrors(e));
    }

    @ExceptionHandler({DataIntegrityViolationException.class,
            AdminUpdateStatusException.class,
            UserUpdateStatusException.class,
            RequestCreationException.class,
            HttpMessageNotReadableException.class,
            FullEventException.class,
            PermissionException.class,
            RequestStatusException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final Exception e) {
        log.error("Нарушение правил целостности БД {}", e.getMessage(), e);
        return new ApiError(e.getMessage(),
                "Нарушение правил целостности БД",
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now().format(CommonUtils.DATE_TIME_FORMATTER),
                getErrors(e));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Exception e) {
        log.error("Необработанная ошибка: {}", e.getMessage(), e);
        return new ApiError(e.getMessage(),
                "Непредвиденная ошибка",
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                LocalDateTime.now().format(CommonUtils.DATE_TIME_FORMATTER),
                getErrors(e));
    }

    private String getErrors(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
