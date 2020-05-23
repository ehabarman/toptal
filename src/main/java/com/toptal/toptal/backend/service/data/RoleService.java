package com.toptal.toptal.backend.service.data;

import com.toptal.toptal.backend.model.Role;
import com.toptal.toptal.backend.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Role entity service
 *
 * @author ehab
 */
@Component
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public void save(Role role) {
        roleRepository.save(role);
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findRoleByName(roleName);
    }

    public boolean existsByName(String roleName) {
        return roleRepository.existsByName(roleName);
    }
}
