package com.toptal.backend.errors.CustomExceptions;

/**
 * Exception for where query filtering
 */
public class BadWhereGrammerException extends RuntimeException {

    public BadWhereGrammerException(String msg) {
        super(msg);
    }
}
