package com.toptal.backend.controller.data;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.model.Role;
import com.toptal.backend.model.User;
import com.toptal.backend.payload.request.UserRequest;
import com.toptal.backend.payload.response.UserResponse;
import com.toptal.backend.security.RoleType;
import com.toptal.backend.util.helpers.StringUtil;
import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;

import static com.toptal.backend.util.Constants.JWT_TOKEN_HEADER_NAME;
import static com.toptal.backend.util.Constants.TOKEN_PREFIX;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest extends BaseControllerTest {

    @DataProvider
    public static Object[][] getUserInfoDataProvider() {
        return new Object[][]{
                {0, Arrays.asList(HttpStatus.OK, HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED)},
                {1, Arrays.asList(HttpStatus.OK, HttpStatus.OK, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND)},
                {2, Arrays.asList(HttpStatus.OK, HttpStatus.OK, HttpStatus.OK, HttpStatus.NOT_FOUND)},
                {3, Arrays.asList(HttpStatus.OK, HttpStatus.OK, HttpStatus.OK, HttpStatus.OK)},
        };
    }

    @UseDataProvider("getUserInfoDataProvider")
    @Test
    public void getUserInfoTest(int caseIndex, List<HttpStatus> expectedMapping) {
        initUsers();
        String[][] usersCredentials = getUsersCredentials();
        String token = getTokenFromLogin(usersCredentials[caseIndex][0], usersCredentials[caseIndex][1]);
        setTokenHeader(token);
        for (int j = 0; j < usersCredentials.length; j++) {
            String userInfoUrl = StringUtil.format(userApiPath, port, usersCredentials[j][0]);
            ResponseEntity<UserResponse> details = restTemplate.getForEntity(userInfoUrl, UserResponse.class);
            Assert.assertEquals(expectedMapping.get(j), details.getStatusCode());
        }
    }

    private String[][] getUsersCredentials() {
        return new String[][]{
                {"user", PASSWORD},
                {"manager", PASSWORD},
                {"admin", PASSWORD},
                {systemAdminUsername, systemAdminPassword}
        };
    }

    private void initUsers() {
        Role useRole = roleService.getRoleByName(RoleType.USER.name());
        Role managerRole = roleService.getRoleByName(RoleType.USER_MANAGER.name());
        Role adminRole = roleService.getRoleByName(RoleType.USER_ADMIN.name());
        User user = new User("user", encoder.encode(PASSWORD), new ArrayList<>(Collections.singletonList(useRole)));
        User manager = new User("manager", encoder.encode(PASSWORD), new ArrayList<>(Collections.singletonList(managerRole)));
        User admin = new User("admin", encoder.encode(PASSWORD), new ArrayList<>(Collections.singletonList(adminRole)));
        userService.saveUser(user);
        userService.saveUser(manager);
        userService.saveUser(admin);
    }

    @Test
    public void getAllUsernamesTest() {
        initUsers();
        String usersInfoUrl = StringUtil.format(usersApiPath, port);
        String[][] usersCredentials = getUsersCredentials();
        int[] expectedResult = {0, 1, 3, 4};
        for (int i = 0; i < usersCredentials.length; i++) {
            String token = getTokenFromLogin(usersCredentials[i][0], usersCredentials[i][1]);
            setTokenHeader(token);
            ResponseEntity details = restTemplate.getForEntity(usersInfoUrl, Object.class);
            if (usersCredentials[i][0].equals("user")) {
                Assert.assertEquals(HttpStatus.UNAUTHORIZED, details.getStatusCode());
            } else {
                if (expectedResult[i] != ((List) details.getBody()).size()) {
                    Assert.fail(StringUtil.format("Failed at case %s", i));
                }
            }
        }
    }

    @DataProvider
    public static Object[][] deleteUserTestDataProvider() {
        return new Object[][]{
                {0, 0, false},
                {0, 1, true},
                {0, 2, true},
                {0, 3, true},
                {1, 0, true},
                {1, 1, false},
                {1, 2, true},
                {1, 3, true},
                {3, 0, false},
                {3, 1, false},
                {3, 2, false},
                {3, 3, true},

        };
    }

    @UseDataProvider("deleteUserTestDataProvider")
    @Test
    public void deleteUserTest(int requester, int target, boolean shouldBeExits) {
        initUsers();
        String[][] usersCredentials = getUsersCredentials();
        String username = usersCredentials[requester][0];
        String password = usersCredentials[requester][1];
        String targetUser = usersCredentials[target][0];
        String token = getTokenFromLogin(username, password);
        setTokenHeader(token);
        String deleteApiPath = StringUtil.format(userApiPath, port, targetUser);
        try {
            restTemplate.delete(deleteApiPath);
            Assert.assertEquals(shouldBeExits, userService.existsByUsername(targetUser));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @DataProvider
    public static Object[][] updateUserDataProvider() {
        return new Object[][]{
                {0, Arrays.asList("user", "manager", "admin", "system-admin")},
                {1, Arrays.asList("", "manager", "admin", "system-admin")},
                {2, Arrays.asList("", "", "admin", "system-admin")},
                {3, Arrays.asList("system-admin@toptal-test.com", "system-admin@toptal-test.com", "system-admin@toptal-test.com",
                        "system-admin")}
        };
    }

    @UseDataProvider("updateUserDataProvider")
    @Test
    public void updateUserTest(int targetUser, List<String> expected) {
        initUsers();
        String[][] usersCredentials = getUsersCredentials();
        String putApiTarget = StringUtil.format(userApiPath, port, usersCredentials[targetUser][0]);
        for (int i = 0; i < usersCredentials.length; i++) {
            String newEmail = usersCredentials[i][0];
            UserRequest userRequest = new UserRequest();
            userRequest.setEmail(newEmail);
            String token = getTokenFromLogin(usersCredentials[i][0], usersCredentials[i][1]);
            HttpHeaders headers = buildHeaders(token);
            HttpEntity request = new HttpEntity(userRequest, headers);
            restTemplate.exchange(putApiTarget, HttpMethod.PATCH, request, Void.class);
            Assert.assertEquals(expected.get(i), userService.findByUsername(usersCredentials[targetUser][0]).getEmail());
        }
    }

    @Test
    public void httpRequestsWithNonExistingUsers() {
        initUsers();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        setTokenHeader(token);
        String userUrl = StringUtil.format(userApiPath, port, "nonExistentUser");

        ResponseEntity details = restTemplate.getForEntity(userUrl, Object.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, details.getStatusCode());

        restTemplate.delete(userUrl);

        HttpHeaders headers = buildHeaders(token);
        HttpEntity request = new HttpEntity(new UserRequest(), headers);
        ResponseEntity responseEntity = restTemplate.exchange(userUrl, HttpMethod.PATCH, request, Void.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void updateUserWithInvalidValues() {
        initUsers();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String[][] usersCredentials = getUsersCredentials();
        String userUrl = StringUtil.format(userApiPath, port, usersCredentials[0][0]);

        HttpHeaders headers = buildHeaders(token);
        ResponseEntity response;

        UserRequest invalidCalories = new UserRequest();
        invalidCalories.setCaloriesLimit(-1);
        response = restTemplate.exchange(userUrl, HttpMethod.PATCH, new HttpEntity(invalidCalories, headers), Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        UserRequest unknownRole = new UserRequest();
        unknownRole.setRole("random role");
        response = restTemplate.exchange(userUrl, HttpMethod.PATCH, new HttpEntity(unknownRole, headers), Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        UserRequest invalidRole = new UserRequest();
        invalidRole.setRole(RoleType.SYSTEM_ADMIN.name());
        response = restTemplate.exchange(userUrl, HttpMethod.PATCH, new HttpEntity(invalidRole, headers), Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void updateUserRole() {
        initUsers();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String[][] usersCredentials = getUsersCredentials();
        String targetName = usersCredentials[0][0];
        String userUrl = StringUtil.format(userApiPath, port, targetName);

        HttpHeaders headers = buildHeaders(token);
        UserRequest newRole = new UserRequest();
        newRole.setRole(RoleType.USER_ADMIN.name());
        restTemplate.exchange(userUrl, HttpMethod.PATCH, new HttpEntity(newRole, headers), Void.class);

        Assert.assertEquals(RoleType.USER_ADMIN.name(), userService.findByUsername(targetName).getRoles().get(0).getName());
    }

}
