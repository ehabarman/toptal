package com.toptal.toptal.backend.security.jwt;

import com.google.gson.Gson;
import com.toptal.toptal.backend.errors.api.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The authentication entry point
 *
 * @author ehab
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.info("Authentication error: {}", authException.getMessage());
        ApiError apiError;
        if(authException instanceof InsufficientAuthenticationException) {
            apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Authentication is required");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else if(authException instanceof InternalAuthenticationServiceException) {
            apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Something went wrong during the authentication");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        else {
            apiError = new ApiError(HttpStatus.UNAUTHORIZED, authException.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String responseBody = new Gson().toJson(apiError.toMap());
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(responseBody);
        out.flush();
    }
}
