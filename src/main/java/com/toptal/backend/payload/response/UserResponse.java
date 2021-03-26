package com.toptal.backend.payload.response;

import com.toptal.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link User} response template
 *
 * @author ehab
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 5926468583005150666L;

    private String email;
    private int caloriesLimit;
    private String username;
    private String role;
    private Boolean isPasswordUpdated;

    public UserResponse(String email, int caloriesLimit, String username, String role) {
        this.email = email;
        this.caloriesLimit = caloriesLimit;
        this.username = username;
        this.role = role;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("caloriesLimit", caloriesLimit);
        map.put("username", username);
        map.put("role_name", role);
        return map;
    }
}
