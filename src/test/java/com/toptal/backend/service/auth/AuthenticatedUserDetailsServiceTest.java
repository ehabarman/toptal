package com.toptal.backend.service.auth;

import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.model.Role;
import com.toptal.backend.model.User;
import com.toptal.backend.security.RoleType;
import com.toptal.backend.service.data.RoleService;
import com.toptal.backend.service.data.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthenticatedUserDetailsServiceTest {

    @Autowired
    private AuthenticatedUserDetailsService authenticatedUserDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Test
    public void userDetailsCreationTest() {
        String username = "user";
        String password = "password";
        String roleName = RoleType.USER.name();
        User user = new User(username, password);
        ArrayList<Role> roleList = new ArrayList<>();
        roleList.add(roleService.getRoleByName(roleName));
        user.setRoles(roleList);
        userService.saveUser(user);
        UserDetails userDetails = authenticatedUserDetailsService.loadUserByUsername(username);
        Assert.assertNotNull(userDetails);
        Assert.assertEquals(username, userDetails.getUsername());
        Assert.assertEquals(password, userDetails.getPassword());
        Assert.assertEquals("ROLE_" + roleName, ((List)userDetails.getAuthorities()).get(0).toString());
    }

}
