package com.toptal.backend.DTO.auth;

import com.toptal.backend.model.Role;
import com.toptal.backend.service.auth.AuthenticatedUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Data transfer object for {@link AuthenticatedUserDetails}
 *
 * @author ehab
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUserDTO {

	private String username;
	private String password;
	private List<Role> role;

}