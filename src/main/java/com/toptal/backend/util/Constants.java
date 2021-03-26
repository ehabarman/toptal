package com.toptal.backend.util;

/**
 * Contains the constants shared by the entire application
 *
 * @author ehab
 */
public class Constants {

    /*****************************
     * Jwt token properties
     *****************************/
    public static final String JWT_TOKEN_HEADER_NAME = "JWT-Token";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final int PREFIX_LENGTH = TOKEN_PREFIX.length();

    /*****************************
     * Application APIs paths
     *****************************/
    public final static String API_USER_URL = "/api/user/%s";
    public final static String API_RECORD_URL = "/api/user/%s/record/%s";
    public final static String API_MEAL_URL = "/api/user/%s/record/%s/meal/%s";
}
