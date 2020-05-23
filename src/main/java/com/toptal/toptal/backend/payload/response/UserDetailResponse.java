package com.toptal.toptal.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * User patch response template
 *
 * @author ehab
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponse implements Serializable {

    private static final long serialVersionUID = 5926468583005150666L;

    private String email;
    private int caloriesLimit;
    private String username;
    private String role;
    private Boolean isPasswordUpdated;

    public UserDetailResponse(String email, int caloriesLimit, String username, String role) {
        this.email = email;
        this.caloriesLimit = caloriesLimit;
        this.username = username;
        this.role = role;
    }
}
