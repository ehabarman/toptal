package com.toptal.backend.controller.data;

import com.toptal.backend.payload.request.RecordRequest;
import com.toptal.backend.payload.response.RecordResponse;
import com.toptal.backend.service.data.MealService;
import com.toptal.backend.service.data.RecordService;
import com.toptal.backend.service.data.RoleService;
import com.toptal.backend.service.data.UserService;
import com.toptal.backend.util.helpers.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.toptal.backend.util.Constants.JWT_TOKEN_HEADER_NAME;
import static com.toptal.backend.util.Constants.TOKEN_PREFIX;

public abstract class BaseControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected UserService userService;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected RecordService recordService;

    @Autowired
    protected MealService mealService;

    @Autowired
    protected PasswordEncoder encoder;

    @Value("${security.credentials.admin.username}")
    protected String systemAdminUsername;

    @Value("${security.credentials.admin.password}")
    protected String systemAdminPassword;

    protected static final String authApiPath = "http://localhost:%s/api/auth/%s";

    protected static final String userApiPath = "http://localhost:%s/api/user/%s";

    protected static final String usersApiPath = "http://localhost:%s/api/users";

    protected static final String recordApiPath = "http://localhost:%s/api/user/%s/record";

    protected static final String recordsApiPath = "http://localhost:%s/api/user/%s/records";

    protected static final String recordPath = "http://localhost:%s/api/user/%s/record/%s";

    protected static final String mealApiPath = "http://localhost:%s/api/user/%s/record/%s/meal";

    protected static final String mealsApiPath = "http://localhost:%s/api/user/%s/record/%s/meals";

    protected static final String mealPath = "http://localhost:%s/api/user/%s/record/%s/meal/%s";

    protected static final String PASSWORD = "password";

    protected String getTokenFromLogin(String username, String password) {
        String loginUrl = StringUtil.format(authApiPath, port, "/login");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = buildCredentialsMap(username, password);
        HttpEntity<Map<String, String>> request = new HttpEntity(body, headers);
        ResponseEntity<Map> result = restTemplate.postForEntity(loginUrl, request, Map.class);
        return (String) result.getBody().get("jwtToken");
    }

    protected Map<String, String> buildCredentialsMap(String username, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        return map;
    }

    protected HttpHeaders buildHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(JWT_TOKEN_HEADER_NAME, TOKEN_PREFIX + token);
        return headers;
    }

    protected void setTokenHeader(String token) {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((getRequest, getBody, execution) -> {
                    getRequest.getHeaders()
                            .add(JWT_TOKEN_HEADER_NAME, TOKEN_PREFIX + token);
                    return execution.execute(getRequest, getBody);
                }));
    }
}
