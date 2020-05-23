package com.toptal.toptal.backend.controller.data;

import com.toptal.toptal.backend.errors.CustomExceptions.NotRemovableResourceException;
import com.toptal.toptal.backend.errors.CustomExceptions.ResourceNotFoundException;
import com.toptal.toptal.backend.model.Role;
import com.toptal.toptal.backend.model.User;
import com.toptal.toptal.backend.payload.request.UserPatchRequest;
import com.toptal.toptal.backend.payload.response.UserDetailResponse;
import com.toptal.toptal.backend.security.RoleType;
import com.toptal.toptal.backend.service.auth.AuthenticatedUserDetails;
import com.toptal.toptal.backend.service.data.RoleService;
import com.toptal.toptal.backend.service.data.UserService;
import com.toptal.toptal.backend.util.helpers.CollectionUtil;
import com.toptal.toptal.backend.util.helpers.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

/**
 * Controller for {@link User} api calls
 *
 * @author ehab
 */
@RestController
@RequestMapping("/api")
public class UserController {

    public static final String API_USER = "/api/user/";

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Returns a specific user details
     */
    @GetMapping("/user/{username}")
    @PostAuthorize("#username  == #principal.getName() or hasRole('USER_ADMIN') or hasRole('USER_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public UserDetailResponse getUserInfo(@PathVariable("username") String username, Principal principal) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(API_USER + username);
        }
        RoleType foundUserRoleType = getRoleTypeFromUser(user);
        List<Role> roles = getRolesFromPrincipal(principal);
        String roleName = getRoleName(roles);
        switch (getRoleType(roleName)) {
            case USER:
            case SYSTEM_ADMIN:
                break;
            case USER_MANAGER:
                if (!username.equals(principal.getName()) && foundUserRoleType != RoleType.USER) {
                    throw new ResourceNotFoundException(API_USER + username);
                }
                break;
            case USER_ADMIN:
                if (foundUserRoleType == RoleType.SYSTEM_ADMIN) {
                    throw new ResourceNotFoundException(API_USER + username);
                }
                break;
            default:
                throw new ResourceNotFoundException(API_USER + username);

        }
        return new UserDetailResponse(user.getEmail(), user.getCalLimit(), user.getUsername(), foundUserRoleType.name());
    }

    /**
     * Returns a list of users' names which the request owner can view and edit
     */
    @GetMapping("/users")
    public List<String> getAllUsernames(@RequestParam(name = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                        Principal principal) {
        List<User> users = getAllUsers(pageSize, pageNumber, principal);
        return CollectionUtil.transformList(users, User::getUsername);
    }

    /**
     * Returns all users' details which the request owner role can view and edit
     */
    @GetMapping("/users/detail")
    public List<UserDetailResponse> getAllUsersDetails(@RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                       @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                                       Principal principal) {
        List<User> users = getAllUsers(pageSize, pageNumber, principal);
        return CollectionUtil.transformList(users, user ->
                new UserDetailResponse(user.getEmail(), user.getCalLimit(), user.getUsername(), getRoleName(user)));
    }

    /**
     * Get all users which the request owner role can view and edit
     */
    private List<User> getAllUsers(Integer pageSize, Integer pageNumber, Principal principal) {
        List<Role> roles = getRolesFromPrincipal(principal);
        String roleName = getRoleName(roles);
        List<User> users;
        if (isValidForPagination(pageSize, pageNumber)) {
            users = userService.getAllUsersNames(getRoleType(roleName), pageSize, pageNumber);
        } else {
            users = userService.getAllUsersNames(getRoleType(roleName));
        }
        return users;
    }

    @DeleteMapping("/user/{username}")
    @PostAuthorize("#username  == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Map<String, Object> deleteUser(@PathVariable("username") String username, Principal principal) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(API_USER + username);
        }
        RoleType foundUserRoleType = getRoleTypeFromUser(user);
        if (foundUserRoleType == RoleType.SYSTEM_ADMIN) {
            throw new NotRemovableResourceException(username + " is not a removable user");
        }
        userService.deleteUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "The user deleted successfully");
        response.put("status", HttpStatus.OK);
        return response;
    }

    @PatchMapping("/user/{username}")
    @PostAuthorize("#username  == #principal.getName() or hasRole('USER_ADMIN') or hasRole('USER_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public UserDetailResponse patchUser(@PathVariable("username") String username, @RequestBody UserPatchRequest patch, Principal principal) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(API_USER + username);
        }
        RoleType foundUserRoleType = getRoleTypeFromUser(user);
        RoleType loggedUserRoleType = getRoleTypeFromPrincipal(principal);
        String email = patch.getEmail();
        String password = StringUtil.isNullOrEmpty(patch.getPassword()) ? null : encoder.encode(patch.getPassword());
        Integer calLimit = patch.getCaloriesLimit();
        if (calLimit != null && calLimit < 0) {
            throw new IllegalArgumentException(StringUtil.format("calLimit can't have negative value"));
        }

        List<Role> roles = null;
        boolean isAdmin = loggedUserRoleType == RoleType.SYSTEM_ADMIN || loggedUserRoleType == RoleType.USER_ADMIN;
        if (isAdmin && StringUtil.isntNullNorEmpty(patch.getRole())) {
            RoleType newRole = RoleType.getRoleTypeByName(patch.getRole());
            if (newRole == null) {
                throw new IllegalArgumentException(StringUtil.format("Unknown role type"));
            }
            roles = Arrays.asList(roleService.getRoleByName(patch.getRole()));
        }

        switch (foundUserRoleType) {
            case USER:
                userService.updateUser(username, email, password, calLimit, roles);
                break;
            case USER_MANAGER:
            case USER_ADMIN:
                if (isAdmin || username.equals(principal.getName())) {
                    userService.updateUser(username, email, password, calLimit, roles);
                } else {
                    throw new ResourceNotFoundException(API_USER + username);
                }
                break;
            case SYSTEM_ADMIN:
                if(loggedUserRoleType == RoleType.SYSTEM_ADMIN) {
                    userService.updateUser(username, email, password, calLimit, roles);
                } else {
                    throw new ResourceNotFoundException(API_USER + username);
                }
        }
        user = userService.findByUsername(username);
        return new UserDetailResponse(user.getEmail(), user.getCalLimit(), user.getUsername(), getRoleName(user), password != null);
    }

    /**
     * Extracts the user roles from principal object
     */
    private List<Role> getRolesFromPrincipal(Principal principal) {
        return ((AuthenticatedUserDetails) (((UsernamePasswordAuthenticationToken) principal).getPrincipal())).getRoles();
    }

    /**
     * Get role name from a list of roles
     */
    private String getRoleName(List<Role> roles) {
        return roles.get(0).getName();
    }

    /**
     * Get role name from a user
     */
    private String getRoleName(User user) {
        return getRoleName(user.getRoles());
    }

    /**
     * Get role type by role name
     */
    private RoleType getRoleType(String roleName) {
        return RoleType.valueOf(roleName);
    }

    /**
     * Extracts role type from a user
     */
    private RoleType getRoleTypeFromUser(User user) {
        String roleName = getRoleName(user.getRoles());
        return getRoleType(roleName);
    }

    /**
     * Extracts role type from principal
     */
    private RoleType getRoleTypeFromPrincipal(Principal principal) {
        String roleName = getRoleName(getRolesFromPrincipal(principal));
        return getRoleType(roleName);
    }

    /**
     * Validate pagination values
     */
    private boolean isValidForPagination(Integer pageSize, Integer pageNumber) {
        return pageSize != null && pageSize > 0 && pageNumber != null && pageNumber >= 0;
    }
}