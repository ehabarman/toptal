package com.toptal.toptal.backend.controller.auth;

import com.toptal.toptal.backend.DTO.auth.AuthenticatedUserDTO;
import com.toptal.toptal.backend.errors.CustomExceptions.AlreadyUsedException;
import com.toptal.toptal.backend.errors.CustomExceptions.InvalidCredentialsException;
import com.toptal.toptal.backend.model.Role;
import com.toptal.toptal.backend.payload.response.JwtResponse;
import com.toptal.toptal.backend.security.jwt.JwtTokenUtil;
import com.toptal.toptal.backend.payload.request.JwtRequest;
import com.toptal.toptal.backend.security.RoleType;
import com.toptal.toptal.backend.service.auth.AuthenticatedUserDetailsService;
import com.toptal.toptal.backend.service.data.RoleService;
import com.toptal.toptal.backend.service.data.UserService;
import com.toptal.toptal.backend.util.helpers.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.toptal.toptal.backend.util.Constants.EXPIRATION_TIME;

/**
 * Restful controller for Jwt authentication requests
 *
 * @author ehab
 */
@RestController
@RequestMapping("api/auth")
@Slf4j
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticatedUserDetailsService userDetailsService;
    private final UserService userService;
    private final RoleService roleService;

    public JwtAuthenticationController(AuthenticationManager authenticationManager, PasswordEncoder encoder, JwtTokenUtil jwtTokenUtil,
                                       AuthenticatedUserDetailsService userDetailsService, UserService userService, RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JwtResponse createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();
        validateCredentialUsage(username, password);
        authenticate(username, password);
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        log.info(StringUtil.format("%s logged in (%s)", username, token));
        return new JwtResponse(token, StringUtil.format("%s seconds", EXPIRATION_TIME));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map<String, Object> saveUser(@RequestBody AuthenticatedUserDTO authenticatedUserDTO) throws Exception {
        String username = authenticatedUserDTO.getUsername();
        String password = authenticatedUserDTO.getPassword();
        validateCredentialUsage(username, password);

        if (userService.existsByUsername(authenticatedUserDTO.getUsername())) {
            throw new AlreadyUsedException("username", authenticatedUserDTO.getUsername());
        }

        Map<String, Object> map = new HashMap<>();
        AuthenticatedUserDTO newAuthenticatedUserDTO = new AuthenticatedUserDTO(
                authenticatedUserDTO.getUsername(),
                encoder.encode(authenticatedUserDTO.getPassword()),
                getDefaultRole()
        );
        userService.registerUser(newAuthenticatedUserDTO);
        log.info(StringUtil.format("Registered new user %s", username));
        map.put("msg", "user created successfully");
        return map;
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    /**
     * Will throw a MissingServletRequestParameterException exception if username or password are missing
     */
    private void validateCredentialUsage(String username, String password) throws MissingServletRequestParameterException {
        if (StringUtil.isNullOrEmpty(username)) {
            throw new MissingServletRequestParameterException("username", "String");
        }
        if (StringUtil.isNullOrEmpty(password)) {
            throw new MissingServletRequestParameterException("password", "String");
        }
    }

    /**
     * Returns default role for new users {@link RoleType#USER}
     */
    private List<Role> getDefaultRole() {
        return Arrays.asList(roleService.getRoleByName(RoleType.USER.name()));
    }
}