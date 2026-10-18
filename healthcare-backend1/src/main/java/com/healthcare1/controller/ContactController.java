package com.healthcare1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare1.entity.Contact;
import com.healthcare1.repository.ContactRepository;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    // Save Contact Message
    @PostMapping
    public Contact saveContact(@RequestBody Contact contact) {

        return contactRepository.save(contact);
    }
}