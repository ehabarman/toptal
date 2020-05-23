package com.toptal.toptal.backend.errors.handler;

import com.google.gson.Gson;
import com.toptal.toptal.backend.errors.api.ApiError;
import com.toptal.toptal.backend.util.helpers.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles exception from authentication process
 *
 * @author ehab
 */
@Slf4j
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.warn(StringUtil.appendAll("User: ", auth.getName(), " attempted to access the protected URL: ", request.getRequestURI()));
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "unauthorized access");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String responseBody = new Gson().toJson(apiError.toMap());
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(responseBody);
        out.flush();

    }
}
