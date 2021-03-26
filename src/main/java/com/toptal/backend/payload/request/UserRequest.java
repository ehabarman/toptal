package com.toptal.backend.payload.request;

import com.toptal.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * {@link User} request template
 *
 * @author ehab
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150666L;

    private String password;
    private String email;
    private Integer caloriesLimit;
    private String role;
}
