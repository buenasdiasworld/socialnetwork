package main.exception;

import lombok.extern.slf4j.Slf4j;
import main.exception.apierror.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    protected ResponseEntity<ApiError> handleBadRequestException(
            BadRequestException ex, WebRequest req) {
        return handleExceptionInternal(ex, ex.getError(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    protected ResponseEntity<ApiError> handleUserNotAuthorizedException(
            BadCredentialsException ex, WebRequest req) {
        return handleExceptionInternal(ex, new ApiError(ex.getMessage(), "Пользователь не найден"), HttpStatus.UNAUTHORIZED, req);
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    protected ResponseEntity<ApiError> handleUserNotAuthorizedException(
            UsernameNotFoundException ex, WebRequest req) {
        return handleExceptionInternal(ex, new ApiError(ex.getMessage(), "Пользователь не авторизован"), HttpStatus.UNAUTHORIZED, req);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<ApiError> handleGenericException(
            Exception ex, WebRequest req) {
        return handleExceptionInternal(ex, null, HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    protected ResponseEntity<ApiError> handleExceptionInternal(
            Exception ex, ApiError body, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
        }
        log.error(body.getError(), body.getErrorDescription(), ex.getCause());
        return new ResponseEntity<>(body, null, status);
    }
}
