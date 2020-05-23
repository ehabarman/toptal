package com.toptal.toptal.backend.repository;

import com.toptal.toptal.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Role CRUD Jpa
 *
 * @author ehab
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(String name);

    Role findRoleByName(String name);

}
