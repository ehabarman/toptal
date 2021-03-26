package com.toptal.backend.service.data;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.model.Role;
import com.toptal.backend.security.RoleType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @DataProvider
    public static Object[][] existsAndGetByNameDataProvider() {
        return new Object[][]{
                {RoleType.USER.name()},
                {RoleType.USER_MANAGER.name()},
                {RoleType.USER_ADMIN.name()},
                {RoleType.SYSTEM_ADMIN.name()},
        };
    }

    @UseDataProvider("existsAndGetByNameDataProvider")
    @Test
    public void existsAndGetByNameTests(String roleName) {
        Role role = roleService.getRoleByName(roleName);
        boolean exists = roleService.existsByName(roleName);
        Assert.assertTrue(exists);
        Assert.assertNotNull(role);
    }

    @Test
    public void saveDeleteTest() {
        String roleName = "test";
        Assert.assertFalse(roleService.existsByName(roleName));
        Role newRole = new Role(roleName);
        newRole = roleService.save(newRole);
        Assert.assertTrue(roleService.existsByName(roleName));
        roleService.deleteById(newRole.getId());
        Assert.assertFalse(roleService.existsByName(roleName));
    }
}
