package com.toptal.toptal.backend.errors.CustomExceptions;

import com.toptal.toptal.backend.util.helpers.StringUtil;

/**
 * Exception indicates the resource with the given parameter value is already used/created
 *
 * @author ehab
 */
public class AlreadyUsedException extends RuntimeException {

    public AlreadyUsedException(String parameterName, String parameterValue) {
        super(generateMessage(parameterName, parameterValue));
    }

    private static String generateMessage(String parameterName, String parameterValue) {
        return StringUtil.appendAll(parameterName, " value ", parameterValue, " is already used");
    }

}

