package com.toptal.backend.errors.CustomExceptions;

/**
 * Triggered when attempting to remove a resource, which shouldn't be removed
 *
 * @author ehab
 */
public class NotRemovableResourceException extends RuntimeException {

    public NotRemovableResourceException(String msg) {
        super(msg);
    }

}
