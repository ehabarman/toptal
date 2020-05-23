package com.toptal.toptal.backend;

import com.toptal.toptal.backend.model.Role;
import com.toptal.toptal.backend.model.User;
import com.toptal.toptal.backend.security.RoleType;
import com.toptal.toptal.backend.service.data.RoleService;
import com.toptal.toptal.backend.service.data.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Spring application starter
 *
 * @author ehab
 */
@SpringBootApplication
@Slf4j
public class ToptalBackendApplication implements CommandLineRunner {

    @Value("${security.credentials.admin.username}")
    private String systemAdminUsername;

    @Value("${security.credentials.admin.password}")
    private String systemAdminPassword;

    @Value("${security.credentials.admin.email}")
    private String systemAdminEmail;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder encoder;

    public static void main(String[] args){
        SpringApplication.run(ToptalBackendApplication.class, args);
    }

    /**
     * Initializes system basic information at the startup
     */
    @Override
    public void run(String... args) {
        initRoles();
        List<Role> roles = Arrays.asList(roleService.getRoleByName(RoleType.SYSTEM_ADMIN.name()));
        User user = new User();
        user.setUsername(systemAdminUsername);
        user.setEmail(systemAdminEmail);
        user.setPassword(encoder.encode(systemAdminPassword));
        user.setRoles(roles);
        if(userService.existsByUsername(user.getUsername())) {
            userService.updateUserData(user);
            log.info("system-admin user data restored to default");
        } else {
            userService.saveUser(user);
            log.info("system-admin user created successfully");
        }
    }

    /**
     * Initializes users roles at the startup
     */
    private void initRoles() {
        List<RoleType> roles = Arrays.asList(RoleType.USER, RoleType.USER_MANAGER, RoleType.USER_ADMIN, RoleType.SYSTEM_ADMIN);
        for(RoleType roleType : roles) {
            if(!roleService.existsByName(roleType.name())){
                Role role = new Role(roleType.name());
                role.setUsers(new ArrayList<>());
                roleService.save(role);
            }
        }
    }
}
