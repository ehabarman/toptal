package com.toptal.backend.errors.CustomExceptions;

/**
 * Thrown when nutritionix has no matches for the meal foods
 *
 * @author ehab
 */
public class NoFoodMatchesException extends RuntimeException {

    public NoFoodMatchesException(String msg) {
        super(msg);
    }

}
