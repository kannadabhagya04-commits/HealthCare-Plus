
package com.healthcare1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare1.entity.User;
import com.healthcare1.repository.UserRepository;
import com.healthcare1.security.JwtService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity
                    .badRequest()
                    .body("Email is already registered.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }

    // Login User and Generate JWT
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginUser) {

        User user = userRepository.findByEmail(loginUser.getEmail());

        if (user != null
                && loginUser.getPassword() != null
                && passwordEncoder.matches(
                        loginUser.getPassword(),
                        user.getPassword())) {

            String token = jwtService.generateToken(user.getEmail());

            return ResponseEntity.ok(token);
        }

        return ResponseEntity
                .status(401)
                .body("Invalid email or password.");
    }

    // Get Logged-in User Profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity
                    .status(404)
                    .body("User not found.");
        }

        return ResponseEntity.ok(user);
    }

    // Update Logged-in User Profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody User updatedUser,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity
                    .status(404)
                    .body("User not found.");
        }

        // Update only the permitted fields
        if (updatedUser.getFullName() != null) {
            user.setFullName(updatedUser.getFullName());
        }

        if (updatedUser.getPhone() != null) {
            user.setPhone(updatedUser.getPhone());
        }
        // Update Date of Birth
        if (updatedUser.getDateOfBirth() != null) {
            user.setDateOfBirth(updatedUser.getDateOfBirth());
        }
     // Update Emergency Contact
        if (updatedUser.getEmergencyContact() != null) {
            user.setEmergencyContact(
                    updatedUser.getEmergencyContact());
        }
        // Update Address
        if (updatedUser.getAddress() != null) {
            user.setAddress(updatedUser.getAddress());
        }
        
        
        
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }

    // Migrate Existing Plain-text Passwords to BCrypt
    @PostMapping("/migrate-passwords")
    public ResponseEntity<String> migratePasswords() {

        int count = 0;

        for (User user : userRepository.findAll()) {

            String password = user.getPassword();

            if (password != null
                    && !password.startsWith("$2a$")
                    && !password.startsWith("$2b$")
                    && !password.startsWith("$2y$")) {

                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                count++;
            }
        }

        return ResponseEntity.ok(
                count + " user passwords migrated successfully.");
    }

    // Get Total Registered Users
    @GetMapping("/count")
    public long getUserCount() {
        return userRepository.count();
    }
}
