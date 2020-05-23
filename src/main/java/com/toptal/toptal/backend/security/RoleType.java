package com.toptal.toptal.backend.security;

/**
 * System defined roles
 *
 * @author ehab
 */
public enum RoleType {
    USER,
    USER_MANAGER,
    USER_ADMIN,
    SYSTEM_ADMIN;

    public static RoleType getRoleTypeByName(String roleName) {
        for(RoleType role: values()) {
            if(role.name().equals(roleName)) {
                return role;
            }
        }
        return null;
    }
}
