package com.huiun.fizzybudget.userservice.controller;

import com.huiun.fizzybudget.common.entity.User;
import com.huiun.fizzybudget.userservice.dto.UserCreationRequest;
import com.huiun.fizzybudget.userservice.exception.RoleNotFoundException;
import com.huiun.fizzybudget.userservice.exception.UserAlreadyExistsException;
import com.huiun.fizzybudget.userservice.exception.UserNotFoundException;
import com.huiun.fizzybudget.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserByUserId(@PathVariable Long userId) {
        Optional<User> user = userService.findUserByUserId(userId);
        return user.map((ResponseEntity::ok))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findUserByUsername(username);
        return user.map((ResponseEntity::ok))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findUserByEmail(email);
        return user.map((ResponseEntity::ok))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreationRequest userCreationRequest) {
        try {
            User user = new User();
            user.setUsername(userCreationRequest.getUsername());
            user.setEmail(userCreationRequest.getEmail());
            String encryptedPassword = passwordEncoder.encode(userCreationRequest.getPassword());
            user.setPasswordHash(encryptedPassword);
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }
        catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<?> addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            userService.addRoleToUser(userId, roleId);
            return ResponseEntity.ok("Role added successfully");
        }
        catch (UserNotFoundException | RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            userService.removeRoleFromUser(userId, roleId);
            return ResponseEntity.ok("Role removed successfully");
        }
         catch (UserNotFoundException | RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
         }
    }
}