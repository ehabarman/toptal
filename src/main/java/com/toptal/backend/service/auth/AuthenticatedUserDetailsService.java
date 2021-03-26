package com.toptal.backend.service.auth;

import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.model.User;
import com.toptal.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Returns an authenticated user {@link AuthenticatedUserDetails}
 *
 * @author ehab
 */
@Service
@Slf4j
public class AuthenticatedUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.info(StringUtil.format("Username %s doesn't exist in database", username));
            throw new UsernameNotFoundException(StringUtil.format("Username %s doesn't exist in database", username));
        }
        return new AuthenticatedUserDetails(user);
    }

}