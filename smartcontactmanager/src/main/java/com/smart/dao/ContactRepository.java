package com.smart.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contacts;

public interface ContactRepository extends JpaRepository<Contacts, Integer>{
	
	@Query("from Contacts as c where c.user.id=:userId")
	public Page<Contacts> findContactByUser(@Param("userId") int userId, Pageable pageable);

}
