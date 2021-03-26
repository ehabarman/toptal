package com.toptal.backend.controller.data;

import com.toptal.backend.errors.CustomExceptions.NotRemovableResourceException;
import com.toptal.backend.errors.CustomExceptions.ResourceNotFoundException;
import com.toptal.backend.model.Role;
import com.toptal.backend.payload.request.UserRequest;
import com.toptal.backend.payload.response.UserResponse;
import com.toptal.backend.service.data.UserService;
import com.toptal.backend.util.Constants;
import com.toptal.backend.util.helpers.CollectionUtil;
import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.controller.SharedUtils;
import com.toptal.backend.model.User;
import com.toptal.backend.security.RoleType;
import com.toptal.backend.service.auth.AuthenticatedUserDetails;
import com.toptal.backend.service.data.RoleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Api(tags = "Users APIs")
public class UserController {

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
    @PreAuthorize("#username  == #principal.getName() or hasRole('USER_ADMIN') or hasRole('USER_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public UserResponse getUserInfo(@PathVariable("username") String username, Principal principal) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
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
                    throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
                }
                break;
            case USER_ADMIN:
                if (foundUserRoleType == RoleType.SYSTEM_ADMIN) {
                    throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
                }
                break;
            default:
                throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));

        }
        return new UserResponse(user.getEmail(), user.getCalLimit(), user.getUsername(), foundUserRoleType.name());
    }

    /**
     * Returns a list of users' which the request owner can view and edit
     */
    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsernames(@RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                     @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                                     @RequestParam(name = "where", required = false) String whereFilter, Principal principal) {
        List<Role> roles = getRolesFromPrincipal(principal);
        String roleName = getRoleName(roles);
        boolean doPaging = SharedUtils.isValidForPagination(pageSize, pageNumber);
        whereFilter = SharedUtils.processWhereFilterToMYSQLWhereClause(whereFilter);
        List<UserResponse> users = userService.executeDynamicUsersQuery(getRoleType(roleName), whereFilter, pageNumber, pageSize, doPaging);
        return CollectionUtil.transformList(users, UserResponse::toMap);
    }

    /**
     * Delete's a user from database
     */
    @DeleteMapping("/user/{username}")
    @PreAuthorize("#username  == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Map<String, Object> deleteUser(@PathVariable("username") String username, Principal principal) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
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

    /**
     * Updates a user's information except for the username
     */
    @PatchMapping("/user/{username}")
    @PreAuthorize("#username  == #principal.getName() or hasRole('USER_ADMIN') or hasRole('USER_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public UserResponse updateUser(@PathVariable("username") String username, @RequestBody UserRequest userRequest, Principal principal) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
        }
        RoleType foundUserRoleType = getRoleTypeFromUser(user);
        RoleType loggedUserRoleType = getRoleTypeFromPrincipal(principal);
        String email = userRequest.getEmail();
        String password = StringUtil.isNullOrEmpty(userRequest.getPassword()) ? null : encoder.encode(userRequest.getPassword());
        Integer calLimit = userRequest.getCaloriesLimit();
        if (calLimit != null && calLimit < 0) {
            throw new IllegalArgumentException("caloriesLimit can't have negative value");
        }

        List<Role> roles = null;
        boolean isAdmin = loggedUserRoleType == RoleType.SYSTEM_ADMIN || loggedUserRoleType == RoleType.USER_ADMIN;
        if (isAdmin && StringUtil.isntNullNorEmpty(userRequest.getRole())) {
            RoleType newRole = RoleType.getRoleTypeByName(userRequest.getRole());
            if (newRole == null) {
                throw new IllegalArgumentException("Unknown role type");
            }
            if (newRole == RoleType.SYSTEM_ADMIN) {
                throw new IllegalArgumentException("SYSTEM_ADMIN is not allowed to be used");
            }
            roles = Arrays.asList(roleService.getRoleByName(userRequest.getRole()));
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
                    throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
                }
                break;
            case SYSTEM_ADMIN:
                if (loggedUserRoleType == RoleType.SYSTEM_ADMIN) {
                    userService.updateUser(username, email, password, calLimit, roles);
                } else {
                    throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
                }
        }
        user = userService.findByUsername(username);
        return new UserResponse(user.getEmail(), user.getCalLimit(), user.getUsername(), getRoleName(user), password != null);
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
}