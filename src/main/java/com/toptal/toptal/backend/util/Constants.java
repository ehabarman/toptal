package com.toptal.toptal.backend.util;

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
    public static final long EXPIRATION_TIME = 3600; // 1 hour
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final int PREFIX_LENGTH = TOKEN_PREFIX.length();

    /*****************************
     * User's response data keys
     *****************************/
    public static final String EMAIL = "email";
    public static final String CALORIES_LIMIT = "calories limit";
    public static final String USERNAME = "username";
}
