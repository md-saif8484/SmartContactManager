package com.smart.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smart.entities.Users;

public class CustomerUserDetails implements UserDetails{
	
	private Users user;
	
	public CustomerUserDetails(Users user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    // Transform the database role into Spring Security's expected format
	    String role = user.getRole(); // e.g., "Role_User"
	    String formattedRole = role.replace("Role_", "ROLE_").toUpperCase();

	    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(formattedRole);
	    return List.of(simpleGrantedAuthority);
	}


	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

}
