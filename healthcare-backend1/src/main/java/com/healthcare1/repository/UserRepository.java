package com.healthcare1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare1.entity.User;

public interface  UserRepository  extends JpaRepository<User,Long>{

	User findByEmail(String email);
	long count();
	

}
