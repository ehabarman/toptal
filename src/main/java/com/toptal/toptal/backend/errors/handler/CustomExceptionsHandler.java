package com.toptal.toptal.backend.errors.handler;

import com.toptal.toptal.backend.errors.CustomExceptions.AlreadyUsedException;
import com.toptal.toptal.backend.errors.CustomExceptions.InvalidDateException;
import com.toptal.toptal.backend.errors.CustomExceptions.NotRemovableResourceException;
import com.toptal.toptal.backend.errors.CustomExceptions.ResourceNotFoundException;
import com.toptal.toptal.backend.errors.api.ApiError;
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

}
