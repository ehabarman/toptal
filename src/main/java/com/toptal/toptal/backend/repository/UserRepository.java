package com.toptal.toptal.backend.repository;

import com.toptal.toptal.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * User CRUD Jpa
 *
 * @author ehab
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Query(
            value = "SELECT * FROM users " +
                    "JOIN user_role ON user_role.user_id = users.id " +
                    "JOIN roles ON  roles.id = user_role.role_id " +
                    "WHERE roles.name = 'USER'",
            nativeQuery = true
    )
    List<User> findAllNamesForUserManager();

    @Query(
            value = "SELECT * FROM users " +
                    "JOIN user_role ON user_role.user_id = users.id " +
                    "JOIN roles ON  roles.id = user_role.role_id " +
                    "WHERE roles.name = 'USER'",
            nativeQuery = true,
            countQuery = "SELECT COUNT(*) FROM users " +
                    "JOIN user_role ON user_role.user_id = users.id " +
                    "JOIN roles ON  roles.id = user_role.role_id " +
                    "WHERE roles.name = 'USER'"
    )
    Page<User> findAllNamesForUserManager(Pageable pageable);

    @Query(
            value = "SELECT * FROM users " +
                    "JOIN user_role ON user_role.user_id = users.id " +
                    "JOIN roles ON  roles.id = user_role.role_id " +
                    "WHERE roles.name != 'SYSTEM_ADMIN'",
            nativeQuery = true
    )
    List<User> findAllNamesForUserAdmin();

    @Query(
            value = "SELECT * FROM users " +
                    "JOIN user_role ON user_role.user_id = users.id " +
                    "JOIN roles ON  roles.id = user_role.role_id " +
                    "WHERE roles.name != 'SYSTEM_ADMIN'",
            nativeQuery = true,
            countQuery = "SELECT count(*) FROM users " +
                         "JOIN user_role ON user_role.user_id = users.id " +
                         "JOIN roles ON  roles.id = user_role.role_id " +
                         "WHERE roles.name != 'SYSTEM_ADMIN'"
    )
    Page<User> findAllNamesForUserAdmin(Pageable pageable);

    @Query(
            value = "SELECT * FROM users",
            nativeQuery = true
    )
    List<User> findAllNamesForSystemAdmin();

    @Query(
            value = "SELECT * FROM users",
            nativeQuery = true,
            countQuery = "SELECT count(*) FROM users"
    )
    Page<User> findAllNamesForSystemAdmin(Pageable pageable);

    boolean existsByUsername(String username);
}