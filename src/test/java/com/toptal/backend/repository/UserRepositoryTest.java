package com.toptal.backend.repository;

import com.toptal.backend.model.User;
import com.toptal.backend.security.RoleType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSystemAdminCreation() {
        String systemAdmin = "system-admin";
        boolean found = userRepository.existsByUsername(systemAdmin);
        Assert.assertTrue(found);
        User user = userRepository.findByUsername(systemAdmin);
        Assert.assertNotNull(user);
        Assert.assertEquals(RoleType.SYSTEM_ADMIN.name(), user.getRoles().get(0).getName());
    }

    @Test
    public void createUser_findUser_deleteUser() {
        String test = "test";
        User user = new User(test, test);
        User found = userRepository.findByUsername(test);
        Assert.assertNull(found);
        userRepository.save(user);
        found = userRepository.findByUsername(test);
        Assert.assertNotNull(found);
        userRepository.delete(found);
        found = userRepository.findByUsername(test);
        Assert.assertNull(found);
    }
}
