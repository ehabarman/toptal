package com.toptal.backend.controller.auth;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.util.helpers.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.net.HttpRetryException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class JwtAuthenticationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${security.credentials.admin.username}")
    private String systemAdminUsername;

    @Value("${security.credentials.admin.password}")
    private String systemAdminPassword;

    private static String authApiPath = "http://localhost:%s/api/auth/%s";

    @DataProvider
    public static Object[][] getLoginCredentials() {
        return new Object[][]{
                {true, true, HttpStatus.OK, ""},
                {true, false, HttpStatus.BAD_REQUEST, "Required String parameter 'password' is not present"},
                {false, true, HttpStatus.BAD_REQUEST, "Required String parameter 'username' is not present"},
                {false, false, HttpStatus.UNAUTHORIZED, "Invalid username or password"},
        };
    }

    @UseDataProvider("getLoginCredentials")
    @Test
    public void loginTest(boolean addUsername, boolean addPassword, HttpStatus expectedStatus, String expected) {
        String loginUrl = StringUtil.format(authApiPath, port, "/login");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = getCredentialsBody(addUsername, addPassword);
        HttpEntity<Map<String, String>> request = new HttpEntity(body, headers);
        try {
            ResponseEntity<Map> result = restTemplate.postForEntity(loginUrl, request, Map.class);
            HttpStatus actualStatus = result.getStatusCode();
            Assert.assertEquals(expectedStatus, actualStatus);
            if (expectedStatus == HttpStatus.OK) {
                Assert.assertNotNull(result.getBody().get("jwtToken"));
            } else {
                Assert.assertEquals(expected, result.getBody().get("message"));
            }
        } catch (Exception e) {
            int statusCode = ((HttpRetryException) e.getCause()).responseCode();
            Assert.assertEquals(expectedStatus, HttpStatus.valueOf(statusCode));
        }
    }

    private Map<String, String> getCredentialsBody(boolean addUsername, boolean addPassword) {
        Map<String, String> body = new HashMap<>();
        if (addUsername) {
            body.put("username", systemAdminUsername);
        }
        if (addPassword) {
            body.put("password", systemAdminPassword);
        }
        if (!(addUsername || addPassword)) {
            body.put("username", "wrong user");
            body.put("password", "wrong password");
        }
        return body;
    }

    @Test
    public void registerTest() {
        String registerUrl = StringUtil.format(authApiPath, port, "/register");
        String loginUrl = StringUtil.format(authApiPath, port, "/login");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            Map<String, String> body = new HashMap<>();
            body.put("username", systemAdminUsername);
            body.put("password", "password");
            HttpEntity<Map<String, String>> request = new HttpEntity(body, headers);
            ResponseEntity<Map> result = restTemplate.postForEntity(registerUrl, request, Map.class);
            Assert.assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        try {
            Map<String, String> body = new HashMap<>();
            body.put("username", "user");
            body.put("password", "password");
            HttpEntity<Map<String, String>> request = new HttpEntity(body, headers);
            ResponseEntity<Map> result = restTemplate.postForEntity(registerUrl, request, Map.class);
            Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
            result = restTemplate.postForEntity(loginUrl, request, Map.class);
            Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


}
