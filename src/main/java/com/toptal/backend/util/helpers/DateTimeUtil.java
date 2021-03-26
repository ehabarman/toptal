package com.toptal.backend.util.helpers;

import com.toptal.backend.errors.CustomExceptions.InvalidDateException;
import com.toptal.backend.errors.CustomExceptions.InvalidTimeException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilities to work with time and date
 *
 * @author ehab
 */
public class DateTimeUtil {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:m[:s]");

    /**
     * Build LocalDate from a string date
     */
    public static LocalDate buildDate(String rawDate) {
        try {
            return LocalDate.parse(rawDate, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(e.getMessage(), rawDate, e.getErrorIndex(), e);
        }
    }

    /**
     * Build LocalDate from a string date
     */
    public static LocalTime buildTime(String rawTime) {
        try {
            return LocalTime.parse(rawTime, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidTimeException(e.getMessage(), rawTime, e.getErrorIndex(), e);
        }
    }
}
