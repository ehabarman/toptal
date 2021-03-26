package com.toptal.backend.errors.handler;

import com.toptal.backend.errors.CustomExceptions.*;
import com.toptal.backend.errors.api.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handler for custom defined exceptions
 *
 * @author ehab
 */
@ControllerAdvice
@Slf4j
public class CustomExceptionsHandler {

    @ExceptionHandler(value = AlreadyUsedException.class)
    public ResponseEntity<Object> exception(AlreadyUsedException exception) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> exception(ResourceNotFoundException exception) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = NotRemovableResourceException.class)
    public ResponseEntity<Object> exception(NotRemovableResourceException exception) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<Object> exception(UsernameNotFoundException exception) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> exception(IllegalArgumentException exception) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = InvalidDateException.class)
    public ResponseEntity<Object> exception(InvalidDateException exception) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = InvalidTimeException.class)
    public ResponseEntity<Object> exception(InvalidTimeException exception) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = NoFoodMatchesException.class)
    public ResponseEntity<Object> exception(NoFoodMatchesException exception) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = BadWhereGrammerException.class)
    public ResponseEntity<Object> exception(BadWhereGrammerException exception) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = InternalHttpRequestException.class)
    public ResponseEntity<Object> exception(InternalHttpRequestException exception) {
        log.error("Internal Http Request Exception: " + exception.getMessage());
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, please try again later");
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
