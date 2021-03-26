package com.toptal.backend.service.data;

import com.toptal.backend.DTO.auth.AuthenticatedUserDTO;
import com.toptal.backend.model.Role;
import com.toptal.backend.payload.response.UserResponse;
import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.model.User;
import com.toptal.backend.repository.UserRepository;
import com.toptal.backend.security.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * User entity service
 *
 * @author ehab
 */
@Component
public class UserService extends BaseService {

    public static final String CALORIES_LIMIT = "calories_limit";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String ROLE_NAME = "role_name";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Saves a new user in the db
     */
    public User registerUser(AuthenticatedUserDTO user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setRoles(user.getRole());
        return userRepository.save(newUser);
    }

    /**
     * Update user's changeable information:
     * - {@link User#getRoles()}
     * - {@link User#getPassword()}
     * - {@link User#getEmail()}
     * - {@link User#getCalLimit()}
     */
    public User updateUserData(User user) {
        User storedUser = findByUsername(user.getUsername());
        storedUser.setEmail(user.getEmail());
        storedUser.setCalLimit(user.getCalLimit());
        storedUser.setRoles(user.getRoles());
        storedUser.setPassword(user.getPassword());
        return userRepository.save(storedUser);
    }

    /**
     * Saves or update a user directly
     */
    public User saveUser(User user) {
        return userRepository.save(user);
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
        if (email != null) {
            user.setEmail(email);
        }
        if (password != null) {
            user.setPassword(password);
        }
        if (calLimit != null) {
            user.setCalLimit(calLimit);
        }
        if (roles != null) {
            user.setRoles(new ArrayList<>(roles));
        }
        userRepository.save(user);
    }

    /**
     * Executes a native query for a users' details
     *
     * @param whereClause Is used as a custom where clause
     * @param doPaging    Indicates if {@param pageNumber} and {@param pageSize} should be used for paging the query result
     */
    public List<UserResponse> executeDynamicUsersQuery(RoleType queryOwnerRole, String whereClause, Integer pageNumber,
                                                       Integer pageSize, boolean doPaging) {
        String userWhereClause = "";
        if (queryOwnerRole == RoleType.USER_ADMIN) {
            userWhereClause = "WHERE roles.name != 'SYSTEM_ADMIN'";
        } else if (queryOwnerRole == RoleType.USER_MANAGER) {
            userWhereClause = "WHERE roles.name = 'USER'";
        }

        String completeQuery = StringUtil.format(
                "SELECT calories_limit, email, username, role_name " +
                        "FROM ( " +
                        "SELECT calories_limit, email, username, roles.name as role_name " +
                        "FROM users " +
                        "JOIN user_role on users.id = user_role.user_id " +
                        "JOIN roles on user_role.role_id = roles.id %s " +
                        "ORDER BY username ASC) users_details", userWhereClause);

        completeQuery = appendWhereClause(completeQuery, whereClause);
        completeQuery = appendPagingClause(completeQuery, pageNumber, pageSize, doPaging);

        try {
            return jdbcTemplate.query(completeQuery, (rs, rowNum) -> {
                UserResponse user = new UserResponse();
                user.setCaloriesLimit(Integer.parseInt(rs.getString(CALORIES_LIMIT)));
                user.setEmail(rs.getString(EMAIL));
                user.setRole(rs.getString(ROLE_NAME));
                user.setUsername(rs.getString(USERNAME));
                return user;
            });
        } catch (Exception e) {
            badSqlGrammerCustomHandle(e);
            throw new RuntimeException("Invalid Search query");
        }
    }
}
