package com.toptal.backend.errors.CustomExceptions;

import java.time.format.DateTimeParseException;

/**
 * Exception for invalid dates
 */
public class InvalidDateException extends DateTimeParseException {

    public InvalidDateException(String msg, CharSequence text, int index, Throwable exception) {
        super(msg, text, index, exception);
    }
}
