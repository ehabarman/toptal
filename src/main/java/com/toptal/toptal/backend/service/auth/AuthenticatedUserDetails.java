package com.toptal.toptal.backend.service.auth;

import com.toptal.toptal.backend.model.Role;
import com.toptal.toptal.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Authenticated user details
 *
 * @author ehab
 */
public class AuthenticatedUserDetails implements UserDetails {

    private final User user;

    private static final String ROLE_PREFIX = "ROLE_";

    public AuthenticatedUserDetails(User user) {
        this.user = user;
    }

    /**
     * Parses user's roles into granted authorities set
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roleAndPermissions = new HashSet<>();
        List<Role> roles = user.getRoles();
        for (Role role : roles)
        {
            roleAndPermissions.add(ROLE_PREFIX + role.getName());
        }
        String[] roleNames = new String[roleAndPermissions.size()];
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roleAndPermissions.toArray(roleNames));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public List<Role> getRoles() {
        return user.getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
