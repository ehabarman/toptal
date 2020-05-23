package com.toptal.toptal.backend.service.data;

import com.toptal.toptal.backend.DTO.auth.AuthenticatedUserDTO;
import com.toptal.toptal.backend.model.Role;
import com.toptal.toptal.backend.model.User;
import com.toptal.toptal.backend.repository.UserRepository;
import com.toptal.toptal.backend.security.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * User entity service
 *
 * @author ehab
 */
@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Saves a new user in the db
     */
    public void registerUser(AuthenticatedUserDTO user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setRoles(user.getRole());
        userRepository.save(newUser);
    }

    /**
     * Update user's changeable information:
     * - {@link User#getRoles()}
     * - {@link User#getPassword()}
     * - {@link User#getEmail()}
     * - {@link User#getCalLimit()}
     */
    public void updateUserData(User user) {
        User storedUser = findByUsername(user.getUsername());
        storedUser.setEmail(user.getEmail());
        storedUser.setCalLimit(user.getCalLimit());
        storedUser.setRoles(user.getRoles());
        storedUser.setPassword(user.getPassword());
        userRepository.save(storedUser);
    }

    /**
     * Saves or update a user directly
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Check if a user by the given username exists in the db
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Search for user in db using his username
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get all viewable users' names for a given role type with paging support
     */
    public List<User> getAllUsersNames(RoleType queryOwnerRole, int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        switch (queryOwnerRole) {
            case SYSTEM_ADMIN:
                return userRepository.findAllNamesForSystemAdmin(pageable).getContent();
            case USER_ADMIN:
                return userRepository.findAllNamesForUserAdmin(pageable).getContent();
            case USER_MANAGER:
                return userRepository.findAllNamesForUserManager(pageable).getContent();
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Get all viewable users' names for a given role type
     */
    public List<User> getAllUsersNames(RoleType queryOwnerRole) {
        switch (queryOwnerRole) {
            case SYSTEM_ADMIN:
                return userRepository.findAllNamesForSystemAdmin();
            case USER_ADMIN:
                return userRepository.findAllNamesForUserAdmin();
            case USER_MANAGER:
                return userRepository.findAllNamesForUserManager();
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Delete user from database
     */
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    /**
     * update user information
     */
    public void updateUser(String username, String email, String password, Integer calLimit, List<Role> roles) {
        User user = findByUsername(username);
        if(email != null) {
            user.setEmail(email);
        }
        if(password != null) {
            user.setPassword(password);
        }
        if(calLimit != null) {
            user.setCalLimit(calLimit);
        }
        if(roles != null) {
            user.setRoles(new ArrayList<>(roles));
        }
        userRepository.save(user);
    }
}
