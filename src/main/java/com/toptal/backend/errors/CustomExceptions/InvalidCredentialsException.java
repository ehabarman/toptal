package com.toptal.backend.errors.CustomExceptions;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * Exception triggered when attempting to authenticate with a wrong username or password
 *
 * @author ehab
 */
public class InvalidCredentialsException extends BadCredentialsException {

    public InvalidCredentialsException(String msg) {
        super(msg);
    }

}
