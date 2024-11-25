package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Users;

public interface UserRepository extends JpaRepository<Users, Integer> {
	@Query("select u from Users u where u.email=:email")
	public Users getUserByUsername(@Param("email") String email);
	
	boolean existsByEmail(String email);
}
