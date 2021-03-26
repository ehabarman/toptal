package com.toptal.backend.repository;

import com.toptal.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Role CRUD Jpa
 *
 * @author ehab
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(String name);

    Role findRoleByName(String name);

}
