package com.toptal.backend.service.data;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.toptal.backend.DTO.auth.AuthenticatedUserDTO;
import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.model.Role;
import com.toptal.backend.model.User;
import com.toptal.backend.payload.response.UserResponse;
import com.toptal.backend.security.RoleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Test
    public void registerUserTest() {
        List<Role> role = new ArrayList<>();
        role.add(roleService.getRoleByName(RoleType.USER.name()));
        String username = "registerUser";
        String password = "password";
        AuthenticatedUserDTO user = new AuthenticatedUserDTO(username, password, role);
        userService.registerUser(user);
        Assert.assertNotNull(userService.findByUsername(username));
    }

    @Test
    public void updateUserDataTest() {
        List<Role> role = new ArrayList<>();
        role.add(roleService.getRoleByName(RoleType.USER.name()));
        String username = "updateUser";
        String password = "password";
        String updatePassword = "updatePassword";
        User user = new User(username, password, role);
        userService.saveUser(user);
        user.setPassword(updatePassword);
        userService.updateUserData(user);
        Assert.assertEquals(updatePassword, userService.findByUsername(username).getPassword());
    }

    @Test
    public void updateUserDataTest2() {
        List<Role> role = new ArrayList<>();
        role.add(roleService.getRoleByName(RoleType.USER.name()));
        String username = "updateUserTest2";
        String password = "password";
        String newPassword = "newPassword";
        String email = "test email";
        int calLimit = 20;
        List<Role> newRole = new ArrayList<>();
        newRole.add(roleService.getRoleByName(RoleType.USER_MANAGER.name()));

        User user = new User(username, password, role);
        userService.saveUser(user);
        userService.updateUser(username, email, newPassword, calLimit, newRole);
        user = userService.findByUsername(username);

        Assert.assertEquals(email, user.getEmail());
        Assert.assertEquals(newPassword, user.getPassword());
        Assert.assertEquals(calLimit, user.getCalLimit());
        Assert.assertEquals(newRole.get(0).getName(), user.getRoles().get(0).getName());
    }

    @Test
    public void deleteTest() {
        String username = "toBeDeletedUser";
        User user = new User(username, "password");
        userService.saveUser(user);
        Assert.assertTrue(userService.existsByUsername(username));
        user = userService.findByUsername(username);
        userService.deleteUser(user);
        Assert.assertFalse(userService.existsByUsername(username));
    }

    @DataProvider
    public static Object[][] executeDynamicUsersQueryDataProvider() {
        List<User> users = Arrays.asList(
                new User("user1", "password"),
                new User("user2", "password"),
                new User("user3", "password")
        );

        List<User> managers = Arrays.asList(
                new User("manager1", "password"),
                new User("manager2", "password")
        );

        List<User> admins = Arrays.asList(
                new User("admin1", "password")
        );

        RoleType system = RoleType.SYSTEM_ADMIN;
        RoleType admin = RoleType.USER_ADMIN;
        RoleType manager = RoleType.USER_MANAGER;
        return new Object[][] {
                {system, "", null, null, false, users, managers, admins, 7, false, ""},
                {system, "", 0, 3, true, users, managers, admins, 3, false, ""},
                {system, "username = 'user1' or username = 'user2'", null, null, false, users, managers, admins, 2, false, ""},
                {admin, "", null, null, false, users, managers, admins, 6, false, ""},
                {admin, "", 0, 3, true, users, managers, admins, 3, false, ""},
                {admin, "username = 'user1' or username = 'user2'", null, null, false, users, managers, admins, 2, false, ""},
                {manager, "", null, null, false, users, managers, admins, 3, false, ""},
                {manager, "", 1, 2, true, users, managers, admins, 1, false, ""},
                {manager, "username = 'user1' or username = 'user2'", null, null, false, users, managers, admins, 2, false, ""},
                {system, "username2 = 'user1'", null, null, false, users, managers, admins, 2, true, "BadWhereGrammerException"},
                {manager, "username eq 'user1'", null, null, false, users, managers, admins, 2, true, "BadWhereGrammerException"}
        };
    }

    @UseDataProvider("executeDynamicUsersQueryDataProvider")
    @Test
    public void executeDynamicUsersQueryTest(RoleType queryOwnerRole, String whereClause, Integer pageNumber, Integer pageSize,
                                             boolean doPaging, List<User> users, List<User> managers, List<User> admins, int expected,
                                             boolean shouldThrowException, String exceptionClass) {
        try {
            initTestData(users, managers, admins);
            List<UserResponse> result = userService.executeDynamicUsersQuery(queryOwnerRole, whereClause, pageNumber, pageSize, doPaging);
            Assert.assertFalse(shouldThrowException);
            Assert.assertEquals(expected, result.size());
        } catch (Exception exception) {
            Assert.assertTrue(shouldThrowException);
            Assert.assertEquals(exception.getClass().getSimpleName(), exceptionClass);
        }

    }

    private static boolean didInitializeRoles = false;

    private void initTestData(List<User> users, List<User> managers, List<User> admins) {
        if(!didInitializeRoles) {
            Role userRole = roleService.getRoleByName(RoleType.USER.name());
            List<Role> userRoleList = new ArrayList<>();
            userRoleList.add(userRole);
            Role managerRole = roleService.getRoleByName(RoleType.USER_MANAGER.name());
            List<Role> managerRoleList = new ArrayList<>();
            managerRoleList.add(managerRole);
            Role adminRole = roleService.getRoleByName(RoleType.USER_ADMIN.name());
            List<Role> adminRoleList = new ArrayList<>();
            adminRoleList.add(adminRole);
            users.forEach(user -> user.setRoles(userRoleList));
            managers.forEach(manager -> manager.setRoles(managerRoleList));
            admins.forEach(admin -> admin.setRoles(adminRoleList));
            didInitializeRoles = true;
        }

        for(User user: users) {
            userService.saveUser(user);
        }

        for(User manager: managers) {
            userService.saveUser(manager);
        }

        for(User admin: admins) {
            userService.saveUser(admin);
        }
    }

}
