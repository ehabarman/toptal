package com.toptal.backend.repository;

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
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @DataProvider
    public static Object[][] initialRolesDataProvider() {
        return new Object[][]{
                {RoleType.USER.name()},
                {RoleType.USER_MANAGER.name()},
                {RoleType.USER_ADMIN.name()},
                {RoleType.SYSTEM_ADMIN.name()},
        };
    }

    @UseDataProvider("initialRolesDataProvider")
    @Test
    public void initialRolesTest(String roleName) {
        boolean found = roleRepository.existsByName(roleName);
        Assert.assertTrue(found);
        Role role = roleRepository.findRoleByName(roleName);
        Assert.assertNotNull(role);
    }

}
