package com.toptal.backend.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.service.auth.AuthenticatedUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

import static com.toptal.backend.util.Constants.*;

/**
 * Process Jwt token in the requests
 *
 * @author ehab
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticatedUserDetailsService authenticatedUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String jwtToken = parseJwt(request);
            String username = extractUsernameFromToken(jwtToken);
            if (StringUtil.isntNullNorEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = authenticatedUserDetailsService.loadUserByUsername(username);
                // If token is expired, create new token
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        chain.doFilter(request, response);
    }

    private String extractUsernameFromToken(String jwtToken) {
        String username = null;
        if (StringUtil.isntNullNorWhiteSpace(jwtToken)) {
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.warn("Corrupted Jwt token");
            } catch (ExpiredJwtException e) {
                logger.warn("Jwt Token has expired");
            }
        } else {
            logger.warn("Jwt token is missing");
        }
        return username;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(JWT_TOKEN_HEADER_NAME);

        if (StringUtil.isntNullNorEmpty(headerAuth) && headerAuth.startsWith(TOKEN_PREFIX)) {
            return headerAuth.substring(PREFIX_LENGTH);
        }

        return null;
    }
}