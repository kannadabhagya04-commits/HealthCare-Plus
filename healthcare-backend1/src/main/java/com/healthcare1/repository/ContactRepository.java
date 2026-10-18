package com.healthcare1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare1.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact,Long> {
	

}
