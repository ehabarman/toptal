package com.toptal.backend.errors.CustomExceptions;

public class InternalHttpRequestException extends RuntimeException {

    public InternalHttpRequestException(String msg) {
        super(msg);
    }
}